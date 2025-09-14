package com.groupeight.product_service.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.groupeight.product_service.domain.Product;

import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByName(String name);

	@Lock(LockModeType.OPTIMISTIC)
	Optional<Product> findWithLockById(Long id);
}
