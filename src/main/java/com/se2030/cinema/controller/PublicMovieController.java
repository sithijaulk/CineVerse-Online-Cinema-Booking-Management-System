package com.se2030.cinema.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.movie.MovieResponse;
import com.se2030.cinema.dto.seat.SeatViewResponse;
import com.se2030.cinema.dto.showtime.ShowtimeResponse;
import com.se2030.cinema.entity.MovieStatus;
import com.se2030.cinema.service.MovieService;
import com.se2030.cinema.service.ShowtimeService;

@RestController
@RequestMapping("/api/public")
public class PublicMovieController {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;

    public PublicMovieController(MovieService movieService, ShowtimeService showtimeService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MovieResponse>> movies(@RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String ageRating,
            @RequestParam(required = false) LocalDate releaseDate,
            @RequestParam(required = false) MovieStatus status) {
        if (status != null) {
            return ResponseEntity.ok(movieService.listByStatus(status));
        }
        if (title != null || genre != null || language != null || ageRating != null || releaseDate != null) {
            return ResponseEntity.ok(movieService.search(title, genre, language, ageRating, releaseDate));
        }
        return ResponseEntity.ok(movieService.listAll());
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieResponse> movie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getById(id));
    }

    @GetMapping("/showtimes/movie/{movieId}")
    public ResponseEntity<List<ShowtimeResponse>> showtimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showtimeService.listByMovie(movieId));
    }

    @GetMapping("/showtimes/movie/{movieId}/dates")
    public ResponseEntity<List<LocalDate>> showtimeDates(@PathVariable Long movieId) {
        return ResponseEntity.ok(showtimeService.listAvailableDatesForMovie(movieId));
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<SeatViewResponse>> seats(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(showtimeService.getSeatLayout(showtimeId));
    }
}