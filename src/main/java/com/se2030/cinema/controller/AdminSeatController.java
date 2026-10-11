package com.se2030.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.seat.SeatAdminRequest;
import com.se2030.cinema.dto.seat.SeatAdminResponse;
import com.se2030.cinema.service.SeatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/seats")
public class AdminSeatController {

    private final SeatService seatService;

    public AdminSeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public ResponseEntity<List<SeatAdminResponse>> list(@RequestParam(required = false) Long screenId) {
        return ResponseEntity.ok(seatService.list(screenId));
    }

    @PostMapping
    public ResponseEntity<SeatAdminResponse> create(@Valid @RequestBody SeatAdminRequest request) {
        return ResponseEntity.ok(seatService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatAdminResponse> update(@PathVariable Long id,
            @Valid @RequestBody SeatAdminRequest request) {
        return ResponseEntity.ok(seatService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}