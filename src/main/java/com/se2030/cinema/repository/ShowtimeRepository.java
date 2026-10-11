package com.se2030.cinema.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se2030.cinema.entity.Showtime;
import com.se2030.cinema.entity.ShowtimeStatus;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovieId(Long movieId);

    List<Showtime> findByScreenIdAndShowDate(Long screenId, LocalDate showDate);

    Optional<Showtime> findByScreenIdAndShowDateAndStartTime(Long screenId, LocalDate showDate, LocalTime startTime);

    List<Showtime> findByStatus(ShowtimeStatus status);
}