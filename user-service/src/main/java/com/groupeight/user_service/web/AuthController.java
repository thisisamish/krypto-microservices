package com.groupeight.user_service.web;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupeight.user_service.domain.User;
import com.groupeight.user_service.infrastructure.UserRepository;
import com.groupeight.user_service.security.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserRepository users;

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody LoginRequest req) {
    // identifier can be username OR email
    authManager.authenticate(new UsernamePasswordAuthenticationToken(req.identifier(), req.password()));

    User user = users.findByUsername(req.identifier())
        .or(() -> users.findByEmail(req.identifier()))
        .orElseThrow();

    String token = jwtService.generate(user);
    return Map.of(
        "accessToken", token,
        "tokenType", "Bearer",
        "expiresIn", 900,
        "roles", List.of(user.getUserRole().name())
    );
  }

  public record LoginRequest(String identifier, String password) {}
}
