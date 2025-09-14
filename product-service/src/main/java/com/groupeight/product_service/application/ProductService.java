package com.groupeight.product_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.groupeight.product_service.web.dto.ProductCreateRequestDto;
import com.groupeight.product_service.web.dto.ProductResponseDto;
import com.groupeight.product_service.web.dto.ProductUpdateRequestDto;

public interface ProductService {
	public ProductResponseDto create(ProductCreateRequestDto dto);

	public void delete(Long id);

	public Page<ProductResponseDto> list(Pageable pageable);

	public ProductResponseDto get(Long id);

	public ProductResponseDto update(Long id, ProductUpdateRequestDto dto);
}
