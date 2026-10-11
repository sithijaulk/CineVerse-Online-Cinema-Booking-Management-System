package com.se2030.cinema.dto.reservation;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "Showtime id is required") Long showtimeId,
        @NotEmpty(message = "Select at least one seat") List<Long> seatIds,
        @NotBlank(message = "Payment method is required") String paymentMethod,
        Boolean simulateSuccess) {
}