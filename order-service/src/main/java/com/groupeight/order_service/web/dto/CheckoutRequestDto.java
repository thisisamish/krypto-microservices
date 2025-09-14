package com.groupeight.order_service.web.dto;

import com.groupeight.order_service.domain.PaymentMethod;
import com.groupeight.order_service.web.dto.OrderDetailDto.AddressDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDto {
	@NotNull
	private PaymentMethod paymentMethod;
	@Valid
	@NotNull
	private AddressDto shippingAddress;
	@Size(max = 512)
	private String notes;
}