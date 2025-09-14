
package com.groupeight.product_service.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupeight.product_service.application.ProductService;
import com.groupeight.product_service.web.dto.ProductCreateRequestDto;
import com.groupeight.product_service.web.dto.ProductResponseDto;
import com.groupeight.product_service.web.dto.ProductUpdateRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for CRUD on products.")
public class ProductController {
	private final ProductService productService;

	@GetMapping
	@Operation(summary = "Get a paginated + sorted list of products")
	public ResponseEntity<Page<ProductResponseDto>> getAllProducts(Pageable pageable) {
		return ResponseEntity.ok(productService.list(pageable));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get product by ID")
	public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.get(id));
	}

	@Operation(summary = "Create a product. Requires ADMIN role.")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductCreateRequestDto dto) {
		ProductResponseDto createdProduct = productService.create(dto);

		return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
	}

	@Operation(summary = "Update a product by ID. Requires ADMIN role.")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponseDto> update(@PathVariable Long id,
			@Valid @RequestBody ProductUpdateRequestDto dto) {
		ProductResponseDto updatedProduct = productService.update(id, dto);

		return ResponseEntity.ok(updatedProduct);
	}

	@Operation(summary = "Delete a product by ID. Requires ADMIN role.")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		productService.delete(id);

		return ResponseEntity.noContent().build();
	}
}
