package com.groupeight.order_service.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.groupeight.order_service.domain.OrderStatus;
import com.groupeight.order_service.domain.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderSummaryDto {
	private final Long userId;
	private final String orderNumber;
	private final OrderStatus status;
	private final BigDecimal grandTotal;
	private final Instant createdAt;
	private final String usernameSnapshot;
	private final PaymentStatus paymentStatus;
}
