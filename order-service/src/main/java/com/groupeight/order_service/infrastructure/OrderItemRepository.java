package com.groupeight.order_service.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupeight.order_service.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
