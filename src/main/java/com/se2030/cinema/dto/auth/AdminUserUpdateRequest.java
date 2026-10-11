package com.se2030.cinema.dto.auth;

import com.se2030.cinema.entity.Role;
import com.se2030.cinema.entity.UserStatus;

import jakarta.validation.constraints.NotNull;

public record AdminUserUpdateRequest(
        @NotNull(message = "Role is required") Role role,
        @NotNull(message = "Status is required") UserStatus status) {
}
