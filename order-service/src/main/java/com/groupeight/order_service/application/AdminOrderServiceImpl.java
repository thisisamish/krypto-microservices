package com.groupeight.order_service.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groupeight.order_service.domain.Address;
import com.groupeight.order_service.domain.Order;
import com.groupeight.order_service.domain.OrderItem;
import com.groupeight.order_service.infrastructure.OrderRepository;
import com.groupeight.order_service.web.dto.OrderDetailDto;
import com.groupeight.order_service.web.dto.OrderDetailDto.AddressDto;
import com.groupeight.order_service.web.dto.OrderDetailDto.OrderItemDto; // <-- use the nested DTO
import com.groupeight.order_service.web.dto.OrderSummaryDto;
import com.groupeight.order_service.web.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderServiceImpl implements AdminOrderService {

	private final OrderRepository orderRepository;

	@Override
	public Page<OrderSummaryDto> list(String orderNumber, String status, String paymentStatus, String userId,
			String dateFrom, String dateTo, Pageable pageable) {
		// TODO: replace with proper Specifications/Predicate-based filtering.
		Page<Order> page = orderRepository.findAll(pageable);
		return page.map(this::toSummary);
	}

	@Override
	public OrderDetailDto get(String orderNumber) {
		Order o = orderRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));
		return toDetail(o);
	}

	private OrderSummaryDto toSummary(Order o) {
		return OrderSummaryDto.builder().orderNumber(o.getOrderNumber()).userId(o.getUserId())
				.usernameSnapshot(o.getUsernameSnapshot()).status(o.getStatus() != null ? o.getStatus() : null) // ensure
																												// String
				.paymentStatus(o.getPaymentStatus() != null ? o.getPaymentStatus() : null).grandTotal(o.getGrandTotal())
				.createdAt(o.getCreatedAt()).build();
	}

	private OrderDetailDto toDetail(Order o) {
		Address addr = o.getShippingAddress();
		AddressDto addrDto = (addr == null) ? null
				: AddressDto.builder().fullName(addr.getFullName()).line1(addr.getLine1()).line2(addr.getLine2())
						.city(addr.getCity()).state(addr.getState()).postalCode(addr.getPostalCode())
						.country(addr.getCountry()).phone(addr.getPhone()).build();

		// Build list of the NESTED DTO type expected by OrderDetailDto
		List<OrderItemDto> items = o.getItems().stream().map(this::toItemDto).collect(Collectors.toList());

		return OrderDetailDto.builder().orderNumber(o.getOrderNumber()).userId(o.getUserId())
				.usernameSnapshot(o.getUsernameSnapshot()).status(o.getStatus() != null ? o.getStatus().name() : null)
				.paymentStatus(o.getPaymentStatus() != null ? o.getPaymentStatus().name() : null)
				.paymentMethod(o.getPaymentMethod() != null ? o.getPaymentMethod().name() : null)
				.paymentReference(o.getPaymentReference()).subtotal(o.getSubtotal()).tax(o.getTax())
				.shippingFee(o.getShippingFee()).discount(o.getDiscount()).grandTotal(o.getGrandTotal())
				.createdAt(o.getCreatedAt()).paidAt(o.getPaidAt()).shippingAddress(addrDto).notes(o.getNotes())
				.items(items) // now matches: Collection<? extends OrderDetailDto.OrderItemDto>
				.build();
	}

	private OrderItemDto toItemDto(OrderItem item) {
		return OrderItemDto.builder().productId(item.getProductId()).productName(item.getProductName())
				.unitPrice(item.getUnitPrice()).quantity(item.getQuantity())
				.lineTotal(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()))).build();
	}
}
