package com.se2030.cinema.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se2030.cinema.dto.seat.SeatViewResponse;
import com.se2030.cinema.dto.showtime.ShowtimeRequest;
import com.se2030.cinema.dto.showtime.ShowtimeResponse;
import com.se2030.cinema.entity.Movie;
import com.se2030.cinema.entity.ReservationSeat;
import com.se2030.cinema.entity.ReservationStatus;
import com.se2030.cinema.entity.Screen;
import com.se2030.cinema.entity.Seat;
import com.se2030.cinema.entity.SeatHold;
import com.se2030.cinema.entity.Showtime;
import com.se2030.cinema.entity.ShowtimeStatus;
import com.se2030.cinema.exception.BusinessRuleException;
import com.se2030.cinema.exception.ResourceNotFoundException;
import com.se2030.cinema.repository.MovieRepository;
import com.se2030.cinema.repository.ReservationSeatRepository;
import com.se2030.cinema.repository.ScreenRepository;
import com.se2030.cinema.repository.SeatRepository;
import com.se2030.cinema.repository.SeatHoldRepository;
import com.se2030.cinema.repository.ShowtimeRepository;
import com.se2030.cinema.service.ShowtimeService;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatHoldRepository seatHoldRepository;

    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository, MovieRepository movieRepository,
            ScreenRepository screenRepository, SeatRepository seatRepository,
            ReservationSeatRepository reservationSeatRepository, SeatHoldRepository seatHoldRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.reservationSeatRepository = reservationSeatRepository;
        this.seatHoldRepository = seatHoldRepository;
    }

    @Override
    @Transactional
    public ShowtimeResponse create(ShowtimeRequest request) {
        validateConflict(request.movieId(), request.screenId(), request.showDate(), request.startTime(), null);
        Showtime showtime = buildShowtime(new Showtime(), request);
        return toResponse(showtimeRepository.save(showtime));
    }

    @Override
    @Transactional
    public ShowtimeResponse update(Long id, ShowtimeRequest request) {
        validateConflict(request.movieId(), request.screenId(), request.showDate(), request.startTime(), id);
        Showtime showtime = getEntity(id);
        buildShowtime(showtime, request);
        return toResponse(showtimeRepository.save(showtime));
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Showtime showtime = getEntity(id);
        showtime.setStatus(ShowtimeStatus.CANCELLED);
        showtimeRepository.save(showtime);
    }

    @Override
    @Transactional
    public void reschedule(Long id, ShowtimeRequest request) {
        validateConflict(request.movieId(), request.screenId(), request.showDate(), request.startTime(), id);
        Showtime showtime = getEntity(id);
        buildShowtime(showtime, request);
        showtime.setStatus(ShowtimeStatus.RESCHEDULED);
        showtimeRepository.save(showtime);
    }

    @Override
    public ShowtimeResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    public List<ShowtimeResponse> listAll() {
        return showtimeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<ShowtimeResponse> listByMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<LocalDate> listAvailableDatesForMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId).stream()
                .map(Showtime::getShowDate)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<SeatViewResponse> getSeatLayout(Long showtimeId) {
        Showtime showtime = getEntity(showtimeId);
        List<Seat> seats = seatRepository.findByScreenId(showtime.getScreen().getId());
        List<ReservationSeat> occupied = reservationSeatRepository.findByShowtimeId(showtimeId);
        List<SeatHold> held = seatHoldRepository.findByShowtimeIdAndExpiresAtAfter(showtimeId, LocalDateTime.now());
        Map<Long, ReservationStatus> statusMap = occupied.stream()
                .collect(Collectors.toMap(rs -> rs.getSeat().getId(), rs -> rs.getReservation().getReservationStatus(),
                        (left, right) -> left));
        Map<Long, Boolean> holdMap = held.stream().collect(Collectors.toMap(hold -> hold.getSeat().getId(), hold -> Boolean.TRUE,
            (left, right) -> left));

        return seats.stream().map(seat -> {
            String status = "AVAILABLE";
            if (statusMap.containsKey(seat.getId()) && statusMap.get(seat.getId()) == ReservationStatus.CONFIRMED) {
                status = "BOOKED";
            } else if (holdMap.containsKey(seat.getId())) {
                status = "TEMPORARILY_HELD";
            } else if (statusMap.containsKey(seat.getId())) {
                status = "AVAILABLE";
            }
            return new SeatViewResponse(seat.getId(), seat.getRowName(), seat.getSeatNumber(), seat.getSeatType().name(),
                    status);
        }).toList();
    }

    private Showtime getEntity(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
    }

    private Showtime buildShowtime(Showtime showtime, ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));
        if (movie.getStatus() == com.se2030.cinema.entity.MovieStatus.NO_LONGER_SHOWING) {
            throw new BusinessRuleException("A no-longer-showing movie cannot be scheduled");
        }
        if (!Boolean.TRUE.equals(screen.getActive()) || !Boolean.TRUE.equals(screen.getBranch().getActive())) {
            throw new BusinessRuleException("The selected screen or branch is inactive");
        }

        showtime.setMovie(movie);
        showtime.setScreen(screen);
        showtime.setShowDate(request.showDate());
        showtime.setStartTime(request.startTime());
        showtime.setTicketPrice(request.ticketPrice());
        showtime.setStatus(request.status() == null ? ShowtimeStatus.SCHEDULED : ShowtimeStatus.valueOf(request.status()));
        return showtime;
    }

    private void validateConflict(Long movieId, Long screenId, LocalDate showDate, java.time.LocalTime startTime, Long excludeId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        List<Showtime> existingShows = showtimeRepository.findByScreenIdAndShowDate(screenId, showDate);
        LocalTime requestedEnd = startTime.plusMinutes(movie.getDurationMinutes());
        for (Showtime existing : existingShows) {
            if (excludeId != null && existing.getId().equals(excludeId)) {
                continue;
            }
            if (existing.getStatus() == ShowtimeStatus.CANCELLED) {
                continue;
            }
            LocalTime existingEnd = existing.getStartTime().plusMinutes(existing.getMovie().getDurationMinutes());
            boolean overlaps = startTime.isBefore(existingEnd) && existing.getStartTime().isBefore(requestedEnd);
            if (overlaps) {
                throw new BusinessRuleException("This hall already has another screening during the selected time");
            }
        }
    }

    private ShowtimeResponse toResponse(Showtime showtime) {
        return new ShowtimeResponse(showtime.getId(), showtime.getMovie().getId(), showtime.getMovie().getTitle(),
            showtime.getScreen().getBranch().getBranchName(), showtime.getScreen().getId(), showtime.getScreen().getScreenName(), showtime.getShowDate(),
                showtime.getStartTime(), showtime.getTicketPrice(), showtime.getStatus().name());
    }
}