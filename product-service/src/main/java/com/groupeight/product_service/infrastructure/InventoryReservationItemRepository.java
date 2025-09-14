package com.groupeight.product_service.infrastructure;

import java.time.Instant;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.groupeight.product_service.domain.InventoryReservationItem;

public interface InventoryReservationItemRepository extends JpaRepository<InventoryReservationItem, Long> {

  @Query("""
      select coalesce(sum(i.quantity),0)
        from InventoryReservationItem i
       where i.productId = :productId
         and i.reservation.expiresAt > :now
      """)
  long activeReservedQty(@Param("productId") Long productId, @Param("now") Instant now);
}
