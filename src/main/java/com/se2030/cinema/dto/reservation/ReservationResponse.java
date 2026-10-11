package com.se2030.cinema.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import com.se2030.cinema.entity.PaymentStatus;
import com.se2030.cinema.entity.ReservationStatus;

public record ReservationResponse(
        Long reservationId,
        String bookingReference,
        String customerName,
        String customerEmail,
        String movieTitle,
        String branchName,
        String screenName,
        LocalDate showDate,
        LocalTime startTime,
        String seatSummary,
        BigDecimal totalAmount,
        ReservationStatus reservationStatus,
        PaymentStatus paymentStatus,
        String paymentMethod,
        LocalDateTime bookingDate,
        boolean verified) {
}