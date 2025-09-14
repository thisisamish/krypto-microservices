package com.groupeight.order_service.infrastructure;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.groupeight.order_service.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByOrderNumber(String orderNumber);

	Optional<Order> findByOrderNumberAndUserId(String orderNumber, Long userId);

	Page<Order> findByUserId(Long userId, Pageable pageable);

	Order findByUserId(Long userId);

	@EntityGraph(attributePaths = "items")
	Optional<Order> findWithItemsByOrderNumberAndUserId(String orderNumber, Long userId);
}
