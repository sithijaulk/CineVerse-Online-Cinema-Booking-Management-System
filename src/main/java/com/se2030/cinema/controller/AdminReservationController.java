package com.se2030.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.reservation.ReservationResponse;
import com.se2030.cinema.service.ReservationService;

@RestController
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
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

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}