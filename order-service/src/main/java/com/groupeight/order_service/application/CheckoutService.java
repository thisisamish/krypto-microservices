package com.groupeight.order_service.application;

import com.groupeight.order_service.web.dto.CheckoutRequestDto;
import com.groupeight.order_service.web.dto.OrderResponseDto;

public interface CheckoutService {
	OrderResponseDto placeOrder(Long userId, CheckoutRequestDto request);
}
