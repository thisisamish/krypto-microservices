package com.groupeight.user_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

/**
 * DTO for returning the currently authenticated user's information.
 */
@Value
@Schema(description = "Represents the currently logged-in user's details")
public class CurrentUserDto {
    @Schema(description = "The username of the logged-in user", example = "amishverma")
    String username;

    @Schema(description = "The role of the logged-in user", example = "ROLE_CUSTOMER")
    String role;
    
    @Schema(description = "The first name of the logged-in user", example = "Amish")
    String firstName;
}
