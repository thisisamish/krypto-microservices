package com.groupeight.user_service.web.dto;

import java.time.Instant;

import com.groupeight.user_service.domain.UserRole;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDetailDto {
    Long id;
    String username;
    UserRole role;
    String email;
    String firstName;
    String lastName;
    Instant createdAt;
}
