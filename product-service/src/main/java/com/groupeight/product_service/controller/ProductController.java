package com.groupeight.product_service.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
  @GetMapping("/hello")
  public Map<String,Object> hello() {
    return Map.of("service","product-service","status","ok");
  }
}
