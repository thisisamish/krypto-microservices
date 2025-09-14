package com.groupeight.product_service.web.dto;

import java.math.BigDecimal;

import com.groupeight.product_service.domain.Product;

public record ProductResponseDto(Long id, String name, String description, BigDecimal price, int stockQuantity,
		String imageUrl) {
	public static ProductResponseDto fromEntity(Product product) {
		return new ProductResponseDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getStockQuantity(), product.getImageUrl());
	}
}