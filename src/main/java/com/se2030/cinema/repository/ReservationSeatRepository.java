package com.se2030.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se2030.cinema.entity.ReservationSeat;
import com.se2030.cinema.entity.ReservationStatus;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    List<ReservationSeat> findByShowtimeId(Long showtimeId);

    List<ReservationSeat> findByReservationId(Long reservationId);

    boolean existsByShowtimeIdAndSeatId(Long showtimeId, Long seatId);

    boolean existsByShowtimeIdAndSeatIdAndReservationReservationStatus(Long showtimeId, Long seatId,
            ReservationStatus reservationStatus);
}