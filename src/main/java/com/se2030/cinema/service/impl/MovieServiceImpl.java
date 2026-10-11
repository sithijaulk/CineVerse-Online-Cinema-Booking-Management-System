package com.se2030.cinema.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se2030.cinema.dto.movie.MovieRequest;
import com.se2030.cinema.dto.movie.MovieResponse;
import com.se2030.cinema.entity.Movie;
import com.se2030.cinema.entity.MovieStatus;
import com.se2030.cinema.exception.BusinessRuleException;
import com.se2030.cinema.exception.ResourceNotFoundException;
import com.se2030.cinema.repository.MovieRepository;
import com.se2030.cinema.service.MovieService;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public MovieResponse create(MovieRequest request) {
        Movie movie = new Movie();
        apply(movie, request);
        return toResponse(movieRepository.save(movie));
    }

    @Override
    @Transactional
    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = getEntity(id);
        apply(movie, request);
        return toResponse(movieRepository.save(movie));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Movie movie = getEntity(id);
        movie.setStatus(MovieStatus.NO_LONGER_SHOWING);
        movieRepository.save(movie);
    }

    @Override
    public MovieResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    public List<MovieResponse> listAll() {
        return movieRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<MovieResponse> listByStatus(MovieStatus status) {
        return movieRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    @Override
    public List<MovieResponse> search(String title, String genre, String language, String ageRating,
            LocalDate releaseDate) {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .filter(movie -> title == null || title.isBlank() || movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(movie -> genre == null || genre.isBlank() || movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(movie -> language == null || language.isBlank() || movie.getLanguage().toLowerCase().contains(language.toLowerCase()))
                .filter(movie -> ageRating == null || ageRating.isBlank() || movie.getAgeRating().toLowerCase().contains(ageRating.toLowerCase()))
                .filter(movie -> releaseDate == null || !movie.getReleaseDate().isBefore(releaseDate))
                .map(this::toResponse)
                .toList();
    }

    private Movie getEntity(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
    }

    private void apply(Movie movie, MovieRequest request) {
        if (request.getReleaseDate() == null || request.getReleaseDate().isBefore(LocalDate.of(1900, 1, 1))) {
            throw new BusinessRuleException("Release date must be a valid date after 1900");
        }
        movie.setTitle(request.getTitle().trim());
        movie.setDescription(request.getDescription().trim());
        movie.setGenre(request.getGenre().trim());
        movie.setLanguage(request.getLanguage().trim());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setAgeRating(request.getAgeRating().trim());
        movie.setCastMembers(request.getCastMembers());
        movie.setDirector(request.getDirector());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setStatus(request.getStatus());
    }

    private MovieResponse toResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setDescription(movie.getDescription());
        response.setGenre(movie.getGenre());
        response.setLanguage(movie.getLanguage());
        response.setDurationMinutes(movie.getDurationMinutes());
        response.setReleaseDate(movie.getReleaseDate());
        response.setAgeRating(movie.getAgeRating());
        response.setCastMembers(movie.getCastMembers());
        response.setDirector(movie.getDirector());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setPosterUrl(movie.getPosterUrl());
        response.setStatus(movie.getStatus());
        return response;
    }
}