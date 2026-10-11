package com.se2030.cinema.dto.screen;

public record ScreenResponse(Long id, Long branchId, String branchName, String screenName, Integer capacity, Boolean active) {
}