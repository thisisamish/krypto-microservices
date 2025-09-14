
package com.groupeight.product_service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupeight.product_service.application.ProductService;
import com.groupeight.product_service.web.dto.ProductResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for CRUD on products.")
public class ProductController {
	private final ProductService productService;

	@GetMapping
	@PageableAsQueryParam
	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	@Operation(summary = "Get paginated products")
	@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
			@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
		return ResponseEntity.ok(productService.getAllProducts(pageable));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	@Operation(summary = "Get product by ID")
	@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
	}
}
