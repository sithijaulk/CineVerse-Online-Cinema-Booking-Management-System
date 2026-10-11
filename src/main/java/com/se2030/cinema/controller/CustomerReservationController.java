package com.se2030.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.reservation.BookingRequest;
import com.se2030.cinema.dto.reservation.ReservationResponse;
import com.se2030.cinema.dto.seat.SeatHoldRequest;
import com.se2030.cinema.dto.seat.SeatHoldResponse;
import com.se2030.cinema.security.UserPrincipal;
import com.se2030.cinema.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/reservations")
public class CustomerReservationController {

    private final ReservationService reservationService;

    public CustomerReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(principal.getId(), request));
    }

    @PostMapping("/holds")
    public ResponseEntity<SeatHoldResponse> hold(@AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SeatHoldRequest request) {
        return ResponseEntity.ok(reservationService.holdSeats(principal.getId(), request));
    }

    @DeleteMapping("/holds/{showtimeId}")
    public ResponseEntity<Void> release(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long showtimeId) {
        reservationService.releaseSeats(principal.getId(), showtimeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponse>> myBookings(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reservationService.getMyBookings(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> byId(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getByIdForUser(id, principal.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        reservationService.cancelForUser(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}