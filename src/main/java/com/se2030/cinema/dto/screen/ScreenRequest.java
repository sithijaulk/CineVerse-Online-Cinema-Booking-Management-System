package com.se2030.cinema.dto.screen;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScreenRequest(
        @NotNull(message = "Branch id is required") Long branchId,
        @NotBlank(message = "Screen name is required") String screenName,
        @Min(value = 1, message = "Capacity must be at least 1") Integer capacity,
        Boolean active) {
}