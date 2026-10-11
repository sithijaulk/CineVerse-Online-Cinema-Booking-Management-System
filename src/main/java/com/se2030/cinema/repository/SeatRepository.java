package com.se2030.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se2030.cinema.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByScreenId(Long screenId);
}