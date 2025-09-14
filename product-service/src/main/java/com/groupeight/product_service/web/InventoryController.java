package com.groupeight.product_service.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.groupeight.product_service.application.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/inventory")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService service;

  // Request/response records match the Feign contract used by order-service
  public record ReservationRequest(List<Item> items, String correlationId) {
    public record Item(Long productId, int quantity) {}
  }
  public record ProductSnapshot(Long productId, String name, BigDecimal price) {}
  public record ReservationResult(boolean ok, String token, String message, List<ProductSnapshot> items) {}
  public record ReservationConfirmation(String token, String orderNumber) {}
  public record ReservationRelease(String token) {}

  @PostMapping("/reserve")
  public ResponseEntity<ReservationResult> reserve(@RequestBody ReservationRequest request) {
    var result = service.reserve(
        new InventoryService.ReservationRequest(
            request.items().stream().map(i -> new InventoryService.ReserveItem(i.productId(), i.quantity())).toList(),
            request.correlationId()));
    var body = new ReservationResult(
        result.ok(), result.token(), result.message(),
        result.items().stream().map(s -> new ProductSnapshot(s.productId(), s.name(), s.price())).toList());
    return ResponseEntity.ok(body);
  }

  @PostMapping("/confirm")
  public ResponseEntity<Void> confirm(@RequestBody ReservationConfirmation request) {
    service.confirm(request.token(), request.orderNumber());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/release")
  public ResponseEntity<Void> release(@RequestBody ReservationRelease request) {
    service.release(request.token());
    return ResponseEntity.noContent().build();
  }
}
