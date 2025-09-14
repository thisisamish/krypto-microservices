package com.groupeight.product_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groupeight.product_service.domain.Product;
import com.groupeight.product_service.infrastructure.ProductRepository;
import com.groupeight.product_service.web.dto.ProductCreateRequestDto;
import com.groupeight.product_service.web.dto.ProductResponseDto;
import com.groupeight.product_service.web.dto.ProductUpdateRequestDto;
import com.groupeight.product_service.web.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	
	@Override
	public Page<ProductResponseDto> list(Pageable pageable) {
		Page<Product> allProducts = productRepository.findAll(pageable);

		return allProducts.map(ProductResponseDto::fromEntity);
	}

	@Override
	public ProductResponseDto get(Long id) {
		return ProductResponseDto.fromEntity(productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found.")));
	}

	@Override
	public ProductResponseDto create(ProductCreateRequestDto dto) {
		Product product = new Product();
		product.setName(dto.name());
		product.setDescription(dto.description());
		product.setPrice(dto.price());
		product.setStockQuantity(dto.stockQuantity());
		product.setImageUrl(dto.imageUrl());
		
		Product savedProduct = productRepository.save(product);

		return ProductResponseDto.fromEntity(savedProduct);
	}
	
	@Override
	public ProductResponseDto update(Long id, ProductUpdateRequestDto dto) {
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product with ID: " + id + " not found."));

		existingProduct.setName(dto.name());
		existingProduct.setDescription(dto.description());
		existingProduct.setPrice(dto.price());
		existingProduct.setStockQuantity(dto.stockQuantity());
		existingProduct.setImageUrl(dto.imageUrl());

		Product updatedProduct = productRepository.save(existingProduct);

		return ProductResponseDto.fromEntity(updatedProduct);
	}

	@Override
	public void delete(Long id) {
		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Product with ID: " + id + " not found.");
		}
		productRepository.deleteById(id);
	}
}