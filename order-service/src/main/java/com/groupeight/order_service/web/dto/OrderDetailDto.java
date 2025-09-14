package com.groupeight.order_service.web.dto;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderDetailDto {
	String orderNumber;
	Long userId; // nullable if user deleted
	String usernameSnapshot;
	String status;
	String paymentStatus;
	String paymentMethod;
	String paymentReference;
	BigDecimal subtotal;
	BigDecimal tax;
	BigDecimal shippingFee;
	BigDecimal discount;
	BigDecimal grandTotal;
	Instant createdAt;
	Instant paidAt;
	AddressDto shippingAddress;
	String notes;

	@Singular
	List<OrderItemDto> items;

	@Value
	@Builder
	public static class OrderItemDto {
		Long productId;
		String productName;
		BigDecimal unitPrice;
		Integer quantity;
		BigDecimal lineTotal;
	}

	@Value
	@Builder
	public static class AddressDto {
		String fullName;
		String line1;
		String line2;
		String city;
		String state;
		String postalCode;
		String country;
		String phone;
	}
}
