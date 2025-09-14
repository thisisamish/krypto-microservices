package com.groupeight.user_service.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupeight.user_service.application.UserService;
import com.groupeight.user_service.web.dto.UserProfileResponseDto;
import com.groupeight.user_service.web.dto.UserProfileUpdateRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> me(Authentication auth) {
		String username = auth.getName();
		UserProfileResponseDto dto = userService.getProfile(username);
		return ResponseEntity.ok(dto);
	}

	@PutMapping("/me")
	public ResponseEntity<UserProfileResponseDto> updateMe(Authentication auth,
			@Valid @RequestBody UserProfileUpdateRequestDto dto) {

		String username = auth.getName();
		UserProfileResponseDto updated = userService.updateProfile(username, dto);
		return ResponseEntity.ok(updated);
	}
}
