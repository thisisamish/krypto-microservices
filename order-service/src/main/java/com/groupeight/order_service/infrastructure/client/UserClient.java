package com.groupeight.order_service.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/internal/users", contextId = "userClient")
public interface UserClient {
	
	@GetMapping("/{id}/default-shipping")
	AddressProjection defaultShipping(@PathVariable Long id);

	record AddressProjection(String fullName, String line1, String line2, String city, String state, String postalCode,
			String country, String phone) {
	}
}
