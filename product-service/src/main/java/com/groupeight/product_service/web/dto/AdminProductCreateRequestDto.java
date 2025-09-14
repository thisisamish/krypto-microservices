package com.groupeight.product_service.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminProductCreateRequestDto {
    @NotBlank
    private String name;
    private String description;
    @NotNull @Min(0)
    private BigDecimal price;
    @NotNull @Min(0)
    private int stock;
    private String imageUrl;
}
