package com.se2030.cinema.dto.showtime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeResponse(
        Long id,
        Long movieId,
        String movieTitle,
        String branchName,
        Long screenId,
        String screenName,
        LocalDate showDate,
        LocalTime startTime,
        BigDecimal ticketPrice,
        String status) {
}