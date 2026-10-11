package com.se2030.cinema.dto.auth;

import com.se2030.cinema.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminUserCreateRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 80, message = "Full name must be between 2 and 80 characters")
        String fullName,
        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email address")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must contain exactly 10 digits")
        String phone,
        @NotNull(message = "Role is required") Role role) {
}
