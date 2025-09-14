package com.groupeight.order_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.groupeight.order_service.domain.Order;
import com.groupeight.order_service.infrastructure.OrderRepository;
import com.groupeight.order_service.web.dto.OrderResponseDto;
import com.groupeight.order_service.web.dto.OrderSummaryDto;
import com.groupeight.order_service.web.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	@Override
	public OrderResponseDto getMyOrder(String orderNumber, Long userId) {
		Order o = orderRepository.findWithItemsByOrderNumberAndUserId(orderNumber, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
		return CheckoutServiceImpl.OrderMapper.toResponseDto(o);
	}

	@Override
	public Page<OrderSummaryDto> listMyOrders(Long userId, Pageable pageable) {
		return orderRepository.findByUserId(userId, pageable).map(o -> new OrderSummaryDto(userId, o.getOrderNumber(),
				o.getStatus(), o.getGrandTotal(), o.getCreatedAt(), o.getUsernameSnapshot(), o.getPaymentStatus()));
	}
}
