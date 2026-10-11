package com.se2030.cinema.service;

import java.util.List;

import com.se2030.cinema.dto.reservation.BookingRequest;
import com.se2030.cinema.dto.reservation.ReservationResponse;
import com.se2030.cinema.dto.seat.SeatHoldRequest;
import com.se2030.cinema.dto.seat.SeatHoldResponse;

public interface ReservationService {

    ReservationResponse createReservation(Long userId, BookingRequest request);

    SeatHoldResponse holdSeats(Long userId, SeatHoldRequest request);

    void releaseSeats(Long userId, Long showtimeId);

    ReservationResponse getById(Long id);

    ReservationResponse getByIdForUser(Long id, Long userId);

    ReservationResponse getByBookingReference(String bookingReference);

    List<ReservationResponse> getMyBookings(Long userId);

    List<ReservationResponse> search(String term);

    void cancel(Long reservationId);

    void cancelForUser(Long reservationId, Long userId);

    ReservationResponse verifyByReference(String bookingReference, Long staffUserId);

    void cleanupExpiredHolds();
}