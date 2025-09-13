package com.groupeight.user_service.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  @GetMapping("/hello")
  public Map<String, Object> hello() {
    return Map.of("service", "user-service", "status", "ok");
  }
}
