package com.groupeight.product_service.web;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.groupeight.product_service.infrastructure.InventoryReservationItemRepository;
import com.groupeight.product_service.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class InternalProductQueryController {

  private final ProductRepository productRepo;
  private final InventoryReservationItemRepository reservationItemRepo;

  public record ProductInfo(Long id, String name, java.math.BigDecimal price, long availableQuantity) {}

  @GetMapping("/{id}")
  public ProductInfo get(@PathVariable Long id) {
    var p = productRepo.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    long reserved = reservationItemRepo.activeReservedQty(id, Instant.now());
    long available = Math.max(0, p.getStockQuantity() - reserved);
    return new ProductInfo(p.getId(), p.getName(), p.getPrice(), available);
  }
}
