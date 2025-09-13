package com.groupeight.order_service.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.groupeight.order_service.client.ProductClient;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
  private final ProductClient productClient;

  @GetMapping("/hello")
  public Map<String,Object> hello() {
    return Map.of("service","order-service","status","ok");
  }

  @GetMapping("/check-products")
  public Map<String,Object> checkProducts() {
    return productClient.hello();
  }
}
