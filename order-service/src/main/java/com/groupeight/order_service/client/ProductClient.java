package com.groupeight.order_service.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "product-service")
public interface ProductClient {
	@GetMapping("/products/hello")
	Map<String, Object> hello();
}
