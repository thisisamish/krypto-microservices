package com.groupeight.order_service.application;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groupeight.order_service.domain.Cart;
import com.groupeight.order_service.domain.CartItem;
import com.groupeight.order_service.infrastructure.CartItemRepository;
import com.groupeight.order_service.infrastructure.CartRepository;
import com.groupeight.order_service.infrastructure.client.ProductQueryClient;
import com.groupeight.order_service.web.dto.CartItemResponseDto;
import com.groupeight.order_service.web.dto.CartResponseDto;
import com.groupeight.order_service.web.exception.InvalidCartOperationException;
import com.groupeight.order_service.web.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductQueryClient productClient;

	@Override
	@Transactional
	public CartResponseDto addItem(Long userId, Long productId, int quantity) {
		if (quantity < 1)
			throw new InvalidCartOperationException("Quantity must be >= 1");

		Cart cart = cartRepository.findWithItemsByUserId(userId).orElseGet(() -> initCart(userId));

		// canonical product data from product-service
		var p = productClient.get(productId);
		if (p == null)
			throw new ResourceNotFoundException("Product not found: " + productId);

		CartItem item = cart.getItems().stream().filter(ci -> ci.getProductId().equals(productId)).findFirst()
				.orElse(null);

		int newQty = quantity + (item == null ? 0 : item.getQuantity());
		if (p.availableQuantity() < newQty) {
			throw new InvalidCartOperationException("Requested quantity exceeds available stock");
		}

		if (item == null) {
			item = new CartItem();
			item.setCart(cart);
			item.setProductId(p.id());
			item.setProductName(p.name());
			item.setQuantity(quantity);
			item.setUnitPrice(p.price()); // snapshot at time of add
			cart.getItems().add(item);
		} else {
			item.setQuantity(newQty);
			item.setUnitPrice(p.price()); // refresh price on change
			item.setProductName(p.name()); // keep name fresh too
		}
		cartRepository.save(cart);
		return toDto(cart);
	}

	@Override
	@Transactional
	public CartResponseDto updateItem(Long userId, Long productId, int quantity) {
		Cart cart = cartRepository.findWithItemsByUserId(userId)
				.orElseThrow(() -> new InvalidCartOperationException("Cart not found"));

		CartItem item = cart.getItems().stream().filter(ci -> ci.getProductId().equals(productId)).findFirst()
				.orElseThrow(() -> new InvalidCartOperationException("Item not in cart"));

		if (quantity == 0) {
			cart.getItems().remove(item);
			cartItemRepository.delete(item);
		} else {
			var p = productClient.get(productId);
			if (p.availableQuantity() < quantity) {
				throw new InvalidCartOperationException("Requested quantity exceeds available stock");
			}
			item.setQuantity(quantity);
			item.setUnitPrice(p.price()); // snapshot to latest price
			item.setProductName(p.name());
		}

		cartRepository.save(cart);
		return toDto(cart);
	}

	@Override
	@Transactional
	public CartResponseDto removeItem(Long userId, Long productId) {
		Cart cart = cartRepository.findWithItemsByUserId(userId)
				.orElseThrow(() -> new InvalidCartOperationException("Cart not found"));
		cart.getItems().removeIf(ci -> ci.getProductId().equals(productId));
		return toDto(cartRepository.save(cart));
	}

	@Override
	@Transactional
	public void clearCart(Long userId) {
		cartRepository.findWithItemsByUserId(userId).ifPresent(cart -> {
			cart.getItems().clear();
			cartRepository.save(cart);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public CartResponseDto getMyCart(Long userId) {
		Cart cart = cartRepository.findWithItemsByUserId(userId).orElseGet(() -> createEmptyCart(userId));
		return toDto(cart);
	}

	// --- helpers ---

	private Cart createEmptyCart(Long userId) {
		Cart cart = new Cart();
		cart.setUserId(userId);
		cart.setItems(List.of());
		return cart;
	}

	private Cart initCart(Long userId) {
		Cart cart = new Cart();
		cart.setUserId(userId);
		return cartRepository.save(cart);
	}

	private CartResponseDto toDto(Cart cart) {
		List<CartItemResponseDto> items = cart.getItems().stream()
				.sorted(Comparator.comparing(CartItem::getProductName, String.CASE_INSENSITIVE_ORDER))
				.map(ci -> CartItemResponseDto.builder().productId(ci.getProductId()).productName(ci.getProductName())
						.unitPrice(ci.getUnitPrice()).quantity(ci.getQuantity())
						.lineTotal(ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()))).build())
				.toList();

		BigDecimal subtotal = items.stream().map(CartItemResponseDto::getLineTotal).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		int count = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();

		return CartResponseDto.builder().items(items).subtotal(subtotal).itemCount(count).build();
	}
}
