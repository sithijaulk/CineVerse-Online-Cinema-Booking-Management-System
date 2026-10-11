package com.se2030.cinema.dto.seat;

public record SeatAdminResponse(Long id, Long screenId, String screenName, String rowName, Integer seatNumber,
        String seatType, Boolean active) {
}