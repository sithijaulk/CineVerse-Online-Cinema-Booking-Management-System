package com.se2030.cinema.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se2030.cinema.entity.SeatHold;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    List<SeatHold> findByUserId(Long userId);

    List<SeatHold> findByShowtimeIdAndExpiresAtAfter(Long showtimeId, LocalDateTime now);

    List<SeatHold> findByUserIdAndShowtimeIdAndExpiresAtAfter(Long userId, Long showtimeId, LocalDateTime now);

    List<SeatHold> findByUserIdAndShowtimeId(Long userId, Long showtimeId);

    List<SeatHold> findByExpiresAtBefore(LocalDateTime cutoff);

    boolean existsByShowtimeIdAndSeatIdAndExpiresAtAfter(Long showtimeId, Long seatId, LocalDateTime now);
}