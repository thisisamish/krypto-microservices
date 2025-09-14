package com.groupeight.user_service.web;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.groupeight.user_service.application.UserService;
import com.groupeight.user_service.web.dto.AdminCreateRequestDto;
import com.groupeight.user_service.web.dto.UserDetailDto;
import com.groupeight.user_service.web.dto.UserProfileResponseDto;
import com.groupeight.user_service.web.dto.UserProfileUpdateRequestDto;
import com.groupeight.user_service.web.dto.UserSummaryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Management")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

	private final UserService service;

	public enum RoleFilter {
		ADMIN, CUSTOMER
	}

	@Operation(summary = "Get a paginated + sorted list of users (admins + customers)")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping
	public Page<UserSummaryDto> list(@RequestParam(required = false) RoleFilter role,
			@RequestParam(required = false) String q,
			@ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
		return service.list(role, q, pageable); // change service signature to accept enum (nullable)
	}

	@Operation(summary = "Get user detail by username (admin or customer)")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/{username:.+}")
	public UserDetailDto get(@PathVariable String username) {
		return service.getByUsername(username);
	}

	@Operation(summary = "Create an admin")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<UserDetailDto> create(@Valid @RequestBody AdminCreateRequestDto dto) {
		UserDetailDto created = service.createUser(dto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{username}")
				.buildAndExpand(created.getUsername()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Operation(summary = "Update user detail by username")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@PutMapping("/{username:.+}")
	public UserDetailDto update(@PathVariable String username, @Valid @RequestBody UserProfileUpdateRequestDto dto) {
		return service.updateUser(username, dto);
	}

	@Operation(summary = "Delete a user (superadmin cannot be deleted)")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("/{username:.+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String username) {
		service.deleteUserByUsername(username);
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<UserProfileResponseDto> me(Authentication auth) {
		String username = auth.getName();
		UserProfileResponseDto dto = service.getProfile(username);
		return ResponseEntity.ok(dto);
	}

	@PutMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<UserProfileResponseDto> updateMe(Authentication auth,
			@Valid @RequestBody UserProfileUpdateRequestDto dto) {

		String username = auth.getName();
		UserProfileResponseDto updated = service.updateProfile(username, dto);
		return ResponseEntity.ok(updated);
	}
}
