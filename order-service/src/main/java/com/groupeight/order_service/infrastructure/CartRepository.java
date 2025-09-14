package com.groupeight.order_service.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.groupeight.order_service.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUserId(Long userId);

	@EntityGraph(attributePaths = "items")
	Optional<Cart> findWithItemsByUserId(Long userId);
}
