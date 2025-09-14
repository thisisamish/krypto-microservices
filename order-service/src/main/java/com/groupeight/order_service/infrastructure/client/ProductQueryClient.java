package com.groupeight.order_service.infrastructure.client;

import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
	    name = "product-service",
	    path = "/internal/products",
	    contextId = "productQueryClient",
	    configuration = com.groupeight.order_service.config.FeignSecurityConfig.class
	)
public interface ProductQueryClient {
	
	@GetMapping("/{id}")
	ProductInfo get(@PathVariable Long id);

	// Projection owned by ORDER service
	record ProductInfo(Long id, String name, BigDecimal price, long availableQuantity) {
	}
}
