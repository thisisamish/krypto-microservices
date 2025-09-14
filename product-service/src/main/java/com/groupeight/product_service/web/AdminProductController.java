package com.groupeight.product_service.web;

import com.groupeight.product_service.application.AdminProductService;
import com.groupeight.product_service.web.dto.AdminProductCreateRequestDto;
import com.groupeight.product_service.web.dto.AdminProductUpdateRequestDto;
import com.groupeight.product_service.web.dto.ProductResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Products")
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

	private final AdminProductService adminProductService;

	@Operation(summary = "List products")
	@GetMapping
	public ResponseEntity<Page<ProductResponseDto>> list(@ParameterObject Pageable pageable) {
		return ResponseEntity.ok(adminProductService.list(pageable));
	}

	@Operation(summary = "Get product by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> get(@PathVariable Long id) {
		return ResponseEntity.ok(adminProductService.get(id));
	}

	@Operation(summary = "Create product")
	@PostMapping
	public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody AdminProductCreateRequestDto dto) {
		ProductResponseDto createdProduct = adminProductService.create(dto);
		
		return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
	}

	@Operation(summary = "Update product")
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponseDto> update(@PathVariable Long id,
			@Valid @RequestBody AdminProductUpdateRequestDto dto) {
		ProductResponseDto updatedProduct = adminProductService.update(id, dto);
		
		return ResponseEntity.ok(updatedProduct);
	}

	@Operation(summary = "Delete product")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		adminProductService.delete(id);
		
		return ResponseEntity.noContent().build();
	}
}
