package com.groupeight.product_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.groupeight.product_service.web.dto.AdminProductCreateRequestDto;
import com.groupeight.product_service.web.dto.AdminProductUpdateRequestDto;
import com.groupeight.product_service.web.dto.ProductResponseDto;

public interface AdminProductService {
    Page<ProductResponseDto> list(Pageable pageable);
    ProductResponseDto get(Long id);
    ProductResponseDto create(AdminProductCreateRequestDto dto);
    ProductResponseDto update(Long id, AdminProductUpdateRequestDto dto);
    void delete(Long id);
}
