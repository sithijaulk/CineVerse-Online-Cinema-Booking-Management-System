package com.se2030.cinema.service;

import com.se2030.cinema.dto.auth.RegisterRequest;
import com.se2030.cinema.dto.auth.ProfileUpdateRequest;
import com.se2030.cinema.dto.auth.UserResponse;
import com.se2030.cinema.entity.User;
import java.util.List;

public interface UserService {

    UserResponse registerCustomer(RegisterRequest request);

    User getByEmail(String email);

    User getById(Long id);

    UserResponse getProfile(Long id);

    UserResponse updateProfile(Long id, ProfileUpdateRequest request);

    List<UserResponse> getAllUsers();

    UserResponse updateUserAccess(Long id, com.se2030.cinema.dto.auth.AdminUserUpdateRequest request);

    UserResponse createManagedUser(com.se2030.cinema.dto.auth.AdminUserCreateRequest request);

    void deactivate(Long id);

    void delete(Long id);
}