package com.se2030.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.se2030.cinema.dto.reservation.ReservationResponse;
import com.se2030.cinema.security.UserPrincipal;
import com.se2030.cinema.service.ReservationService;

@RestController
@RequestMapping("/api/staff/reservations")
public class StaffReservationController {

    private final ReservationService reservationService;

    public StaffReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> search(@RequestParam(required = false) String term) {
        return ResponseEntity.ok(reservationService.search(term));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ReservationResponse> byReference(@PathVariable String reference) {
        return ResponseEntity.ok(reservationService.getByBookingReference(reference));
    }

    @PostMapping("/{reference}/verify")
    public ResponseEntity<ReservationResponse> verify(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String reference) {
        return ResponseEntity.ok(reservationService.verifyByReference(reference, principal.getId()));
    }
}