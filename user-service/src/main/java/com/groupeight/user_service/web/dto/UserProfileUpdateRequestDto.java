package com.groupeight.user_service.web.dto;

import jakarta.validation.constraints.*;

public record UserProfileUpdateRequestDto(
	    @NotBlank @Size(max = 50) String firstName,
	    @Size(max = 50) String middleName,
	    @Size(max = 50) String lastName,
	    @NotBlank @Email String email,
	    @NotBlank @Size(max = 255) String address,
	    @NotBlank @Pattern(regexp = "^\\d{10}$", message = "Contact number must be exactly 10 digits.")
	    String contactNo
	) {}