package com.groupeight.product_service.infrastructure;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.groupeight.product_service.domain.Product;

import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.id in :ids")
  List<Product> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);
}
