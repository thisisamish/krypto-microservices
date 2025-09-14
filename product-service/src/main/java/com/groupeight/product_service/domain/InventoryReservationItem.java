package com.groupeight.product_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
public class InventoryReservationItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_token", referencedColumnName = "token")
  private InventoryReservation reservation;

  private Long productId;
  private int quantity;
}
