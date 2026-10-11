package com.se2030.cinema.dto.dashboard;

public record DashboardStatsResponse(
        long totalMovies,
        long nowShowingMovies,
        long upcomingMovies,
        long totalBranches,
        long totalScreens,
        long totalShowtimes,
        long totalReservations,
        long confirmedReservations,
        long pendingReservations) {
}