package com.groupeight.user_service.web.dto;

public record UserProfileResponseDto(
	    String username,
	    String firstName,
	    String middleName,
	    String lastName,
	    String email,
	    String address,
	    String contactNo
	) {}
