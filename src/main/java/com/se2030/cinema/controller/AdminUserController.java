package com.se2030.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.se2030.cinema.dto.auth.AdminUserUpdateRequest;
import com.se2030.cinema.dto.auth.AdminUserCreateRequest;
import com.se2030.cinema.dto.auth.UserResponse;
import com.se2030.cinema.service.UserService;
import com.se2030.cinema.security.UserPrincipal;
import com.se2030.cinema.exception.BusinessRuleException;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody AdminUserCreateRequest request) {
        return ResponseEntity.ok(userService.createManagedUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserAccess(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null && principal.getId().equals(id)) {
            throw new BusinessRuleException("You cannot delete your own administrator account.");
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}