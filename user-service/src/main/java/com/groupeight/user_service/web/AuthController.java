package com.groupeight.user_service.web;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.groupeight.user_service.application.UserService;
import com.groupeight.user_service.domain.User;
import com.groupeight.user_service.infrastructure.UserRepository;
import com.groupeight.user_service.security.JwtService;
import com.groupeight.user_service.web.dto.UserRegistrationRequestDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserRepository users;
  private final UserService userService;

  // Optional: wire a blacklist if you have one (see interface below)
  private final Optional<TokenBlacklistService> tokenBlacklistService;

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody @Valid LoginRequest req) {
    // Resolve identifier to a username for AuthenticationManager
    User user = users.findByUsernameIgnoreCase(req.identifier())
        .or(() -> users.findByEmailIgnoreCase(req.identifier()))
        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

    try {
      authManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getUsername(), req.password()));
    } catch (AuthenticationException ex) {
      throw new BadCredentialsException("Invalid credentials");
    }

    String token = jwtService.generate(user);

    // If JwtService exposes TTL, use it; otherwise keep a sensible default.
    int expiresIn = (jwtService instanceof JwtWithTtl j) ? j.getAccessTokenTtlSeconds() : 900;

    return Map.of(
        "accessToken", token,
        "tokenType", "Bearer",
        "expiresIn", expiresIn,
        "roles", List.of(user.getUserRole().name())
    );
  }

  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid UserRegistrationRequestDto dto) {
    userService.registerUser(dto);

    // Location points to the user resource by username
    URI location = URI.create("/api/v1/users/" + dto.username());
    return ResponseEntity.created(location).body(Map.of(
        "status", "created",
        "username", dto.username()
    ));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
    // With pure JWT, logout is typically client-side (discard token).
    // If you maintain a denylist, blacklist the presented token here.
    if (authorization != null && authorization.startsWith("Bearer ")) {
      String token = authorization.substring(7);
      tokenBlacklistService.ifPresent(svc -> svc.blacklist(token));
    }
    return ResponseEntity.noContent().build();
  }

  public record LoginRequest(String identifier, String password) {}

  /** Optional: expose TTL if your JwtService supports it. */
  public interface JwtWithTtl {
    int getAccessTokenTtlSeconds();
  }

  /** Optional: plug in a blacklist (e.g., backed by Redis) and have your JWT filter consult it. */
  public interface TokenBlacklistService {
    void blacklist(String token);
    boolean isBlacklisted(String token);
  }
}
