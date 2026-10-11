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

import com.se2030.cinema.dto.screen.ScreenRequest;
import com.se2030.cinema.dto.screen.ScreenResponse;
import com.se2030.cinema.service.ScreenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/screens")
public class AdminScreenController {

    private final ScreenService screenService;

    public AdminScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @GetMapping
    public ResponseEntity<List<ScreenResponse>> list() {
        return ResponseEntity.ok(screenService.listAll());
    }

    @PostMapping
    public ResponseEntity<ScreenResponse> create(@Valid @RequestBody ScreenRequest request) {
        return ResponseEntity.ok(screenService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScreenResponse> update(@PathVariable Long id, @Valid @RequestBody ScreenRequest request) {
        return ResponseEntity.ok(screenService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        screenService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}