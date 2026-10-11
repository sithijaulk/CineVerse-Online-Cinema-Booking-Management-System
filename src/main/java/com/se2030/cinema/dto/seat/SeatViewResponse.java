package com.se2030.cinema.dto.seat;

public record SeatViewResponse(
        Long seatId,
        String rowName,
        Integer seatNumber,
        String seatType,
        String status) {
}