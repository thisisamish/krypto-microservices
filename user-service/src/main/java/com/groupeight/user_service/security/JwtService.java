package com.groupeight.user_service.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.groupeight.user_service.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtEncoder encoder;

  public String generate(User user) {
    Instant now = Instant.now();
    List<String> roles = new ArrayList<>();
    roles.add(user.getUserRole().name());
    if (Boolean.TRUE.equals(user.getIsSuperAdmin())) {
      roles.add("SUPER_ADMIN"); // optional extra role you can gate behind @PreAuthorize
    }

    var claims = JwtClaimsSet.builder()
        .issuer("krypto-user-service")
        .issuedAt(now)
        .expiresAt(now.plusSeconds(15 * 60))
        .subject(user.getId().toString())   // sub = user id
        .claim("username", user.getUsername())
        .claim("email", user.getEmail())
        .claim("roles", roles)              // Spring maps this → ROLE_*
        .build();

    return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}
