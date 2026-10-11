package com.se2030.cinema.dto.showtime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ShowtimeRequest(
        @NotNull(message = "Movie id is required") Long movieId,
        @NotNull(message = "Screen id is required") Long screenId,
        @NotNull(message = "Show date is required") LocalDate showDate,
        @NotNull(message = "Start time is required") LocalTime startTime,
        @NotNull(message = "Ticket price is required") @DecimalMin(value = "0.0", inclusive = true, message = "Ticket price must be positive") BigDecimal ticketPrice,
        String status) {
}