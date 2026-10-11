package com.se2030.cinema.dto.auth;

import com.se2030.cinema.entity.Role;

public record LoginResponse(String message, Long userId, String fullName, String email, Role role) {
}