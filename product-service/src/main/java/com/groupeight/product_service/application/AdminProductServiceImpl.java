package com.groupeight.product_service.application;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.groupeight.product_service.domain.Product;
import com.groupeight.product_service.infrastructure.ProductRepository;
import com.groupeight.product_service.web.dto.AdminProductCreateRequestDto;
import com.groupeight.product_service.web.dto.AdminProductUpdateRequestDto;
import com.groupeight.product_service.web.dto.ProductResponseDto;
import com.groupeight.product_service.web.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductServiceImpl implements AdminProductService {

	private final ProductRepository productRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<ProductResponseDto> list(Pageable pageable) {
		Page<Product> allProducts = productRepository.findAll(pageable);

		return allProducts.map(ProductResponseDto::fromEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponseDto get(Long id) {
		Product p = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		return toDto(p);
	}

	@Override
	public ProductResponseDto create(AdminProductCreateRequestDto dto) {
		Product p = new Product();
		p.setName(dto.getName());
		p.setDescription(dto.getDescription());
		p.setPrice(dto.getPrice());
		p.setStockQuantity(dto.getStock());
		p.setImageUrl(dto.getImageUrl());
		productRepository.save(p);
		return toDto(p);
	}

	@Override
	public ProductResponseDto update(Long id, AdminProductUpdateRequestDto dto) {
		Product p = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		if (dto.getName() != null)
			p.setName(dto.getName());
		if (dto.getDescription() != null)
			p.setDescription(dto.getDescription());
		if (dto.getPrice() != null)
			p.setPrice(dto.getPrice());
		p.setStockQuantity(dto.getStock());
		if (dto.getImageUrl() != null)
			p.setImageUrl(dto.getImageUrl());
		productRepository.save(p);
		return toDto(p);
	}

	@Override
	public void delete(Long id) {
		if (!productRepository.existsById(id))
			throw new ResourceNotFoundException("Product not found");
		productRepository.deleteById(id);
	}

	private ProductResponseDto toDto(Product product) {
		ProductResponseDto dto = new ProductResponseDto(product.getId(), product.getName(), product.getDescription(),
				product.getPrice(), product.getStockQuantity(), product.getImageUrl());
		return dto;
	}
}
