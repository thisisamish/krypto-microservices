package com.groupeight.product_service.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AdminProductUpdateRequestDto {
    private String name;
    private String description;
    @Min(0)
    private BigDecimal price;
    @Min(0)
    private int stock;
    private String imageUrl;
}
