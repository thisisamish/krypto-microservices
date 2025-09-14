package com.groupeight.user_service.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDto(
		@NotBlank(message = "Username cannot be empty.") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores.") String username,
		@NotBlank(message = "First name cannot be empty.") @Size(max = 50, message = "First name cannot exceed 50 characters.") String firstName,
		@Size(max = 50, message = "Middle name cannot exceed 50 characters.") String middleName,
		@Size(max = 50, message = "Last name cannot exceed 50 characters.") String lastName,
		@NotBlank(message = "Email cannot be empty.") @Email(message = "Please provide a valid email address.") String email,
		@NotBlank(message = "Password cannot be blank.") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,100}$", message = "Password must be 8-100 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.") String password,
		@NotBlank(message = "Address cannot be empty.") @Size(max = 255, message = "Address cannot exceed 255 characters.") String address,
		@NotBlank(message = "Contact number cannot be blank.") @Pattern(regexp = "^\\d{10}$", message = "Contact number must be exactly 10 digits.") String contactNo) {

}