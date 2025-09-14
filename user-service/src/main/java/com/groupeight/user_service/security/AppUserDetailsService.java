package com.groupeight.user_service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.groupeight.user_service.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

  private final UserRepository users;

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    // Allow login with username OR email
    return users.findByUsername(identifier)
        .or(() -> users.findByEmail(identifier))
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
  }
}
