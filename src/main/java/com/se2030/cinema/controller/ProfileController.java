package com.se2030.cinema.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.auth.ProfileUpdateRequest;
import com.se2030.cinema.dto.auth.UserResponse;
import com.se2030.cinema.security.UserPrincipal;
import com.se2030.cinema.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> get(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getId()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(@AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(principal.getId(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivate(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deactivate(principal.getId());
        return ResponseEntity.noContent().build();
    }
}