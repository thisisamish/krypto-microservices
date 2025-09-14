package com.groupeight.order_service.application;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groupeight.order_service.domain.Address;
import com.groupeight.order_service.domain.Cart;
import com.groupeight.order_service.domain.Order;
import com.groupeight.order_service.domain.OrderItem;
import com.groupeight.order_service.domain.OrderStatus;
import com.groupeight.order_service.domain.PaymentStatus;
import com.groupeight.order_service.infrastructure.CartRepository;
import com.groupeight.order_service.infrastructure.OrderRepository;
import com.groupeight.order_service.infrastructure.client.ProductClient;
import com.groupeight.order_service.infrastructure.client.ProductClient.ProductSnapshot;
import com.groupeight.order_service.web.dto.CheckoutRequestDto;
import com.groupeight.order_service.web.dto.OrderDetailDto.AddressDto;
import com.groupeight.order_service.web.dto.OrderItemDto;
import com.groupeight.order_service.web.dto.OrderResponseDto;
import com.groupeight.order_service.web.exception.CartEmptyException;
import com.groupeight.order_service.web.exception.InsufficientStockException;
import com.groupeight.order_service.web.exception.PaymentFailedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final PaymentService paymentService;
	private final ProductClient productClient;

	/** Mapper kept local */
	static class OrderMapper {
		static OrderResponseDto toResponseDto(Order o) {
			return OrderResponseDto.builder().orderNumber(o.getOrderNumber()).status(o.getStatus())
					.paymentStatus(o.getPaymentStatus()).paymentMethod(o.getPaymentMethod()).subtotal(o.getSubtotal())
					.tax(o.getTax()).shippingFee(o.getShippingFee()).discount(o.getDiscount())
					.grandTotal(o.getGrandTotal()).createdAt(o.getCreatedAt()).paidAt(o.getPaidAt())
					.shippingAddress(toAddressDto(o.getShippingAddress())).notes(o.getNotes())
					.items(o.getItems().stream()
							.map(i -> OrderItemDto.builder().productId(i.getProductId()).productName(i.getProductName())
									.unitPrice(i.getUnitPrice()).quantity(i.getQuantity()).lineTotal(i.getLineTotal())
									.build())
							.toList())
					.build();
		}

		static AddressDto toAddressDto(Address a) {
			if (a == null)
				return null;

			return AddressDto.builder().fullName(a.getFullName()).line1(a.getLine1()).line2(a.getLine2())
					.city(a.getCity()).state(a.getState()).postalCode(a.getPostalCode()).country(a.getCountry())
					.phone(a.getPhone()).build();
		}
	}

	// --- order number (use DB sequence/ULID in prod) ---
	private String generateOrderNumber() {
		String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
		int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
		return "KSF-" + date + "-" + rand;
	}

	@Override
	@Transactional
	public OrderResponseDto placeOrder(Long userId, CheckoutRequestDto request) {
		Cart cart = cartRepository.findWithItemsByUserId(userId)
				.orElseThrow(() -> new CartEmptyException("Cart not found"));
		if (cart.getItems().isEmpty())
			throw new CartEmptyException("Cart is empty");

		// 1) Reserve stock in product-service and get canonical product snapshots
		var items = cart.getItems().stream()
				.map(ci -> new ProductClient.ReservationRequest.Item(ci.getProductId(), ci.getQuantity())).toList();

		var reserve = productClient.reserve(new ProductClient.ReservationRequest(items, null));
		if (!reserve.ok())
			throw new InsufficientStockException(reserve.message());

		Map<Long, ProductSnapshot> snapById = new ConcurrentHashMap<>();
		reserve.items().forEach(ps -> snapById.put(ps.productId(), ps));

		// 2) Totals based on canonical prices
		BigDecimal subtotal = cart.getItems().stream()
				.map(ci -> snapById.get(ci.getProductId()).price().multiply(BigDecimal.valueOf(ci.getQuantity())))
				.reduce(ZERO, BigDecimal::add);

		BigDecimal tax = ZERO; // apply your policy here
		BigDecimal shipping = ZERO; // apply your policy here
		BigDecimal discount = ZERO; // apply your policy here
		BigDecimal grand = subtotal.add(tax).add(shipping).subtract(discount);

		// 3) Build order aggregate with snapshots (no User/Product entities)
		Order order = new Order();
		order.setOrderNumber(generateOrderNumber());
		order.setUserId(userId);
		order.setStatus(OrderStatus.CREATED);
		order.setPaymentStatus(PaymentStatus.PENDING);
		order.setPaymentMethod(request.getPaymentMethod());
		order.setSubtotal(subtotal);
		order.setTax(tax);
		order.setShippingFee(shipping);
		order.setDiscount(discount);
		order.setGrandTotal(grand);

		AddressDto ad = request.getShippingAddress();
		Address addr = new Address(ad.getFullName(), ad.getLine1(), ad.getLine2(), ad.getCity(), ad.getState(),
				ad.getPostalCode(), ad.getCountry(), ad.getPhone());
		order.setShippingAddress(addr);
		order.setNotes(request.getNotes());

		cart.getItems().forEach(ci -> {
			var ps = snapById.get(ci.getProductId());
			order.addItem(OrderItem.builder().productId(ps.productId()).productName(ps.name()).unitPrice(ps.price())
					.quantity(ci.getQuantity()).lineTotal(ps.price().multiply(BigDecimal.valueOf(ci.getQuantity())))
					.build());
		});

		// 4) Pay → confirm / release
		try {
			var result = paymentService.charge(request.getPaymentMethod(), grand);
			if (!result.success())
				throw new PaymentFailedException("Payment failed: " + result.message());

			order.setPaymentStatus(PaymentStatus.SUCCESS);
			order.setStatus(OrderStatus.PAID);
			order.setPaymentReference(result.reference());
			order.setPaidAt(Instant.now());

			orderRepository.save(order); // persist order
			cart.getItems().clear(); // clear cart
			cartRepository.save(cart);

			productClient.confirm(new ProductClient.ReservationConfirmation(reserve.token(), order.getOrderNumber()));
			return OrderMapper.toResponseDto(order);

		} catch (RuntimeException ex) {
			// release stock if anything goes wrong
			productClient.release(new ProductClient.ReservationRelease(reserve.token()));
			throw ex;
		}
	}
}
