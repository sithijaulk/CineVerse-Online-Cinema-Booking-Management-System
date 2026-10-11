package com.se2030.cinema.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservationSummaryResponse(
        Long showtimeId,
        String movieTitle,
        String branchName,
        String screenName,
        LocalDate showDate,
        LocalTime startTime,
        List<String> selectedSeats,
        Integer ticketCount,
        BigDecimal ticketPrice,
        BigDecimal totalAmount) {
}