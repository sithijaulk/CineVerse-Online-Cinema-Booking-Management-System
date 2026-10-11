package com.se2030.cinema.dto.seat;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SeatHoldRequest(
        @NotNull(message = "Showtime id is required") Long showtimeId,
        @NotEmpty(message = "Select at least one seat") List<Long> seatIds) {
}