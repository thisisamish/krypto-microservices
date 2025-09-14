package com.groupeight.order_service.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.groupeight.order_service.domain.OrderStatus;
import com.groupeight.order_service.domain.PaymentMethod;
import com.groupeight.order_service.domain.PaymentStatus;
import com.groupeight.order_service.web.dto.OrderDetailDto.AddressDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderResponseDto {
	private final String orderNumber;
	private final OrderStatus status;
	private final PaymentStatus paymentStatus;
	private final PaymentMethod paymentMethod;
	private final BigDecimal subtotal;
	private final BigDecimal tax;
	private final BigDecimal shippingFee;
	private final BigDecimal discount;
	private final BigDecimal grandTotal;
	private final Instant createdAt;
	private final Instant paidAt;
	private final AddressDto shippingAddress;
	private final String notes;
	private final List<OrderItemDto> items;
}
