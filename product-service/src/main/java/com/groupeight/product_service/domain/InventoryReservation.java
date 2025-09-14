package com.groupeight.product_service.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class InventoryReservation {

  @Id
  @Column(length = 36)
  private String token; // UUID string

  private Instant createdAt;
  private Instant expiresAt;

  private String correlationId; // optional idempotency / tracing

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InventoryReservationItem> items = new ArrayList<>();

  public static InventoryReservation createWithToken(Instant now, Instant expiresAt, String correlationId) {
    InventoryReservation r = new InventoryReservation();
    r.token = UUID.randomUUID().toString();
    r.createdAt = now;
    r.expiresAt = expiresAt;
    r.correlationId = correlationId;
    return r;
  }

  public void addItem(Long productId, int qty) {
    InventoryReservationItem it = new InventoryReservationItem();
    it.setReservation(this);
    it.setProductId(productId);
    it.setQuantity(qty);
    items.add(it);
  }
}
