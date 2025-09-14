package com.groupeight.user_service.application;

import com.groupeight.user_service.web.dto.UserProfileResponseDto;
import com.groupeight.user_service.web.dto.UserProfileUpdateRequestDto;
import com.groupeight.user_service.web.dto.UserRegistrationRequestDto;

public interface UserService {
	public void registerUser(UserRegistrationRequestDto dto);
	
	UserProfileResponseDto getProfile(String username);
	UserProfileResponseDto updateProfile(String username, UserProfileUpdateRequestDto dto);
}
