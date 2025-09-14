package com.groupeight.order_service.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemDto {
	private final Long productId;
	private final String productName;
	private final BigDecimal unitPrice;
	private final int quantity;
	private final BigDecimal lineTotal;
}
