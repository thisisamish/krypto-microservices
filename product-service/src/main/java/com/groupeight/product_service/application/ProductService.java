package com.groupeight.product_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.groupeight.product_service.web.dto.ProductRequestDto;
import com.groupeight.product_service.web.dto.ProductResponseDto;

public interface ProductService {
	public ProductResponseDto createProduct(ProductRequestDto dto);

	public void deleteProduct(Long id);

	public Page<ProductResponseDto> getAllProducts(Pageable pageable);

	public ProductResponseDto getProductById(Long id);

	public ProductResponseDto updateProduct(Long id, ProductRequestDto dto);
}
