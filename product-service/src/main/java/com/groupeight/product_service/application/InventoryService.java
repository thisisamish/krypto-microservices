package com.groupeight.product_service.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groupeight.product_service.domain.InventoryReservation;
import com.groupeight.product_service.domain.Product;
import com.groupeight.product_service.infrastructure.InventoryReservationItemRepository;
import com.groupeight.product_service.infrastructure.InventoryReservationRepository;
import com.groupeight.product_service.infrastructure.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

  private static final int RESERVE_MINUTES = 15;

  private final ProductRepository productRepo;
  private final InventoryReservationRepository reservationRepo;
  private final InventoryReservationItemRepository itemRepo;

  public record ReserveItem(Long productId, int quantity) {}
  public record ProductSnapshot(Long productId, String name, BigDecimal price) {}
  public record ReservationRequest(List<ReserveItem> items, String correlationId) {}
  public record ReservationResult(boolean ok, String token, String message, List<ProductSnapshot> items) {}

  @Transactional
  public ReservationResult reserve(ReservationRequest req) {
    Instant now = Instant.now();
    reservationRepo.deleteExpired(now); // opportunistic cleanup

    if (req.items() == null || req.items().isEmpty()) {
      return new ReservationResult(false, null, "No items to reserve", List.of());
    }

    // Lock all products we are touching to avoid concurrent overbooking
    List<Long> ids = req.items().stream().map(ReserveItem::productId).distinct().toList();
    List<Product> products = productRepo.findAllByIdForUpdate(ids);
    Map<Long, Product> byId = products.stream().collect(java.util.stream.Collectors.toMap(Product::getId, p -> p));

    // Validate availability using "stock - activeReserved"
    for (ReserveItem it : req.items()) {
      Product p = byId.get(it.productId());
      if (p == null) return new ReservationResult(false, null, "Product not found: " + it.productId(), List.of());
      long reserved = itemRepo.activeReservedQty(p.getId(), now);
      long available = p.getStockQuantity() - reserved;
      if (it.quantity() <= 0 || it.quantity() > available) {
        return new ReservationResult(false, null,
            "Insufficient stock for product " + p.getName() + " (available " + available + ")", List.of());
      }
    }

    // Make reservation
    InventoryReservation res = InventoryReservation.createWithToken(
        now, now.plus(RESERVE_MINUTES, ChronoUnit.MINUTES), req.correlationId());
    req.items().forEach(it -> res.addItem(it.productId(), it.quantity()));
    reservationRepo.save(res);

    // Snapshots for caller (name/price)
    List<ProductSnapshot> snaps = ids.stream()
        .map(id -> {
          Product p = byId.get(id);
          return new ProductSnapshot(p.getId(), p.getName(), p.getPrice());
        }).toList();

    return new ReservationResult(true, res.getToken(), "reserved", snaps);
  }

  @Transactional
  public void confirm(String token, String orderNumber) {
    Instant now = Instant.now();
    var res = reservationRepo.findByToken(token).orElse(null);
    if (res == null) return;                // idempotent
    if (res.getExpiresAt().isBefore(now)) { // expired reservation cannot be confirmed
      reservationRepo.delete(res);
      throw new IllegalStateException("Reservation expired");
    }

    // Lock all products and deduct stock
    List<Long> ids = res.getItems().stream().map(i -> i.getProductId()).distinct().toList();
    List<Product> products = productRepo.findAllByIdForUpdate(ids);
    Map<Long, Product> byId = products.stream().collect(java.util.stream.Collectors.toMap(Product::getId, p -> p));

    res.getItems().forEach(i -> {
      Product p = byId.get(i.getProductId());
      if (p == null) throw new IllegalStateException("Product not found: " + i.getProductId());
      int newQty = p.getStockQuantity() - i.getQuantity();
      if (newQty < 0) throw new IllegalStateException("Negative stock for product " + p.getName());
      p.setStockQuantity(newQty);
    });

    // Persist new stock and remove reservation
    productRepo.saveAll(products);
    reservationRepo.delete(res);
  }

  @Transactional
  public void release(String token) {
    reservationRepo.findByToken(token).ifPresent(reservationRepo::delete);
  }
}
