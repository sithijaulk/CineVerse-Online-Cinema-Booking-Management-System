package com.se2030.cinema.service;

import java.time.LocalDate;
import java.util.List;

import com.se2030.cinema.dto.seat.SeatViewResponse;
import com.se2030.cinema.dto.showtime.ShowtimeRequest;
import com.se2030.cinema.dto.showtime.ShowtimeResponse;

public interface ShowtimeService {

    ShowtimeResponse create(ShowtimeRequest request);

    ShowtimeResponse update(Long id, ShowtimeRequest request);

    void cancel(Long id);

    void reschedule(Long id, ShowtimeRequest request);

    ShowtimeResponse getById(Long id);

    List<ShowtimeResponse> listAll();

    List<ShowtimeResponse> listByMovie(Long movieId);

    List<LocalDate> listAvailableDatesForMovie(Long movieId);

    List<SeatViewResponse> getSeatLayout(Long showtimeId);
}