package com.groupeight.product_service.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductCreateRequestDto(
		@NotBlank(message = "Product name cannot be blank.") @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters.") String name,
		String description,
		@NotNull(message = "Price is required.") @DecimalMin(value = "0.01", message = "Price must be greater than 0.") BigDecimal price,
		@NotNull(message = "Stock quantity is required.") @PositiveOrZero(message = "Stock quantity cannot be negative.") int stockQuantity,
		String imageUrl) {

}