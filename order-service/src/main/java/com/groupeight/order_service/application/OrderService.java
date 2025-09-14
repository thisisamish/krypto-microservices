package com.groupeight.order_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.groupeight.order_service.web.dto.OrderResponseDto;
import com.groupeight.order_service.web.dto.OrderSummaryDto;

public interface OrderService {
	OrderResponseDto getMyOrder(String orderNumber, Long userId);

	Page<OrderSummaryDto> listMyOrders(Long userId, Pageable pageable);
}
