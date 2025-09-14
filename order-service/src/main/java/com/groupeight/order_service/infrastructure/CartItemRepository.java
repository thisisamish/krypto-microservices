package com.groupeight.order_service.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupeight.order_service.domain.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	void deleteByCartIdAndProductId(Long cartId, Long productId);

	Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
