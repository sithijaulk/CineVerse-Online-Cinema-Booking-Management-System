package com.se2030.cinema.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfileUpdateRequest(
        @NotBlank(message = "Full name is required")
        @jakarta.validation.constraints.Size(min = 2, max = 80, message = "Full name must be between 2 and 80 characters")
        String fullName,
        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must contain exactly 10 digits")
        String phone) {
}