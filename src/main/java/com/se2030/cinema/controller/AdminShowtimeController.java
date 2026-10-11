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
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.showtime.ShowtimeRequest;
import com.se2030.cinema.dto.showtime.ShowtimeResponse;
import com.se2030.cinema.service.ShowtimeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/showtimes")
public class AdminShowtimeController {

    private final ShowtimeService showtimeService;

    public AdminShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public ResponseEntity<List<ShowtimeResponse>> list() {
        return ResponseEntity.ok(showtimeService.listAll());
    }

    @PostMapping
    public ResponseEntity<ShowtimeResponse> create(@Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(showtimeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowtimeResponse> update(@PathVariable Long id, @Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(showtimeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        showtimeService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reschedule")
    public ResponseEntity<Void> reschedule(@PathVariable Long id, @Valid @RequestBody ShowtimeRequest request) {
        showtimeService.reschedule(id, request);
        return ResponseEntity.noContent().build();
    }
}