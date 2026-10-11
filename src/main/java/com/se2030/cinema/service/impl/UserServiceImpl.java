package com.se2030.cinema.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.se2030.cinema.dto.auth.RegisterRequest;
import com.se2030.cinema.dto.auth.ProfileUpdateRequest;
import com.se2030.cinema.dto.auth.UserResponse;
import com.se2030.cinema.dto.auth.AdminUserUpdateRequest;
import com.se2030.cinema.dto.auth.AdminUserCreateRequest;
import com.se2030.cinema.entity.Role;
import com.se2030.cinema.entity.User;
import com.se2030.cinema.entity.UserStatus;
import com.se2030.cinema.exception.BusinessRuleException;
import com.se2030.cinema.exception.ResourceNotFoundException;
import com.se2030.cinema.repository.UserRepository;
import com.se2030.cinema.repository.ReservationRepository;
import com.se2030.cinema.repository.SeatHoldRepository;
import com.se2030.cinema.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;
    private final SeatHoldRepository seatHoldRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            ReservationRepository reservationRepository, SeatHoldRepository seatHoldRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.reservationRepository = reservationRepository;
        this.seatHoldRepository = seatHoldRepository;
    }

    @Override
    @Transactional
    public UserResponse registerCustomer(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);

        return toResponse(userRepository.save(user));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserResponse getProfile(Long id) {
        return toResponse(getById(id));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, ProfileUpdateRequest request) {
        User user = getById(id);
        user.setFullName(request.fullName().trim());
        user.setPhone(request.phone());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public UserResponse updateUserAccess(Long id, AdminUserUpdateRequest request) {
        User user = getById(id);
        user.setRole(request.role());
        user.setStatus(request.status());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse createManagedUser(AdminUserCreateRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleException("Email already registered");
        }
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(request.role());
        user.setStatus(UserStatus.ACTIVE);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        User user = getById(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);
        if (reservationRepository.existsByUserId(id)) {
            throw new BusinessRuleException("This user has booking history. Deactivate the account instead of deleting it.");
        }
        seatHoldRepository.deleteAll(seatHoldRepository.findByUserId(id));
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        return response;
    }
}