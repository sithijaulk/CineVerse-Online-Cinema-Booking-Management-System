package com.se2030.cinema.service;

import java.time.LocalDate;
import java.util.List;

import com.se2030.cinema.dto.movie.MovieRequest;
import com.se2030.cinema.dto.movie.MovieResponse;
import com.se2030.cinema.entity.MovieStatus;

public interface MovieService {

    MovieResponse create(MovieRequest request);

    MovieResponse update(Long id, MovieRequest request);

    void deactivate(Long id);

    MovieResponse getById(Long id);

    List<MovieResponse> listAll();

    List<MovieResponse> listByStatus(MovieStatus status);

    List<MovieResponse> search(String title, String genre, String language, String ageRating, LocalDate releaseDate);
}