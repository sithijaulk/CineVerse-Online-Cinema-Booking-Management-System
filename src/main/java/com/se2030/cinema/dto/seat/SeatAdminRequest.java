package com.se2030.cinema.dto.seat;

import com.se2030.cinema.entity.SeatType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatAdminRequest(
        @NotNull Long screenId,
        @NotBlank String rowName,
        @NotNull @Min(1) Integer seatNumber,
        @NotNull SeatType seatType,
        Boolean active) {
}