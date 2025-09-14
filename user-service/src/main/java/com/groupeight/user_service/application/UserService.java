package com.groupeight.user_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.groupeight.user_service.web.UsersController.RoleFilter;
import com.groupeight.user_service.web.dto.AdminCreateRequestDto;
import com.groupeight.user_service.web.dto.UserDetailDto;
import com.groupeight.user_service.web.dto.UserProfileResponseDto;
import com.groupeight.user_service.web.dto.UserProfileUpdateRequestDto;
import com.groupeight.user_service.web.dto.UserRegistrationRequestDto;
import com.groupeight.user_service.web.dto.UserSummaryDto;

import jakarta.validation.Valid;

public interface UserService {

	// Admin endpoints
	Page<UserSummaryDto> list(RoleFilter role, String q, Pageable pageable);
	UserDetailDto getByUsername(String username);
	UserDetailDto createUser(@Valid AdminCreateRequestDto dto);
	UserDetailDto updateUser(String username, @Valid UserProfileUpdateRequestDto dto);
	void deleteUserByUsername(String username);

	// Self-service profile
	UserProfileResponseDto getProfile(String username);
	UserProfileResponseDto updateProfile(String username, UserProfileUpdateRequestDto dto);

	// Public registration (if you expose it elsewhere)
	void registerUser(@Valid UserRegistrationRequestDto dto);
}
