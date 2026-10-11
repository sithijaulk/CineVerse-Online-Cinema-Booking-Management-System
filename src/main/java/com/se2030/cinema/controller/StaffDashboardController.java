package com.se2030.cinema.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.dashboard.DashboardStatsResponse;
import com.se2030.cinema.service.DashboardService;

@RestController
@RequestMapping("/api/staff/dashboard")
public class StaffDashboardController {

    private final DashboardService dashboardService;

    public StaffDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> stats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}