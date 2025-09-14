package com.groupeight.product_service.infrastructure;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;

import com.groupeight.product_service.domain.InventoryReservation;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {

  @Modifying
  @Query("delete from InventoryReservation r where r.expiresAt < :now")
  int deleteExpired(Instant now);

  Optional<InventoryReservation> findByToken(String token);
}
