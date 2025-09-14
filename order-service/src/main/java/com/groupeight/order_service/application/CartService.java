package com.groupeight.order_service.application;

import com.groupeight.order_service.web.dto.CartResponseDto;

public interface CartService {
	CartResponseDto addItem(Long userId, Long productId, int quantity);

	void clearCart(Long userId);

	CartResponseDto getMyCart(Long userId);

	CartResponseDto removeItem(Long userId, Long productId);

	CartResponseDto updateItem(Long userId, Long productId, int quantity);
}
