package com.groupeight.order_service.infrastructure.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", path = "/internal/inventory", contextId = "productClient", configuration = com.groupeight.order_service.config.FeignSecurityConfig.class)
public interface ProductClient {

	@PostMapping("/reserve")
	ReservationResult reserve(@RequestBody ReservationRequest request);

	@PostMapping("/confirm")
	void confirm(@RequestBody ReservationConfirmation confirmation);

	@PostMapping("/release")
	void release(@RequestBody ReservationRelease release);

	record ReservationRequest(List<Item> items, String correlationId) {
		public record Item(Long productId, int quantity) {
		}
	}

	record ProductSnapshot(Long productId, String name, BigDecimal price) {
	}

	record ReservationResult(boolean ok, String token, String message, List<ProductSnapshot> items) {
	}

	record ReservationConfirmation(String token, String orderNumber) {
	}

	record ReservationRelease(String token) {
	}
}
