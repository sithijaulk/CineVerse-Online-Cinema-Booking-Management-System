package com.se2030.cinema.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se2030.cinema.entity.Movie;
import com.se2030.cinema.entity.MovieStatus;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByStatus(MovieStatus status);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenreContainingIgnoreCase(String genre);

    List<Movie> findByLanguageContainingIgnoreCase(String language);

    List<Movie> findByAgeRatingContainingIgnoreCase(String ageRating);

    List<Movie> findByReleaseDateGreaterThanEqual(LocalDate releaseDate);
}