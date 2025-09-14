package com.groupeight.order_service.web.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CartResponseDto {
	private final List<CartItemResponseDto> items;
	private final BigDecimal subtotal;
	private final int itemCount;
}
