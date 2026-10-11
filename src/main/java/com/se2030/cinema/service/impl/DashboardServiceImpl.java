package com.se2030.cinema.service.impl;

import org.springframework.stereotype.Service;

import com.se2030.cinema.dto.dashboard.DashboardStatsResponse;
import com.se2030.cinema.entity.MovieStatus;
import com.se2030.cinema.entity.ReservationStatus;
import com.se2030.cinema.repository.CinemaBranchRepository;
import com.se2030.cinema.repository.MovieRepository;
import com.se2030.cinema.repository.ReservationRepository;
import com.se2030.cinema.repository.ScreenRepository;
import com.se2030.cinema.repository.ShowtimeRepository;
import com.se2030.cinema.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final MovieRepository movieRepository;
    private final CinemaBranchRepository branchRepository;
    private final ScreenRepository screenRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ReservationRepository reservationRepository;

    public DashboardServiceImpl(MovieRepository movieRepository, CinemaBranchRepository branchRepository,
            ScreenRepository screenRepository, ShowtimeRepository showtimeRepository,
            ReservationRepository reservationRepository) {
        this.movieRepository = movieRepository;
        this.branchRepository = branchRepository;
        this.screenRepository = screenRepository;
        this.showtimeRepository = showtimeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public DashboardStatsResponse getStats() {
        return new DashboardStatsResponse(
                movieRepository.count(),
                movieRepository.findByStatus(MovieStatus.NOW_SHOWING).size(),
                movieRepository.findByStatus(MovieStatus.COMING_SOON).size(),
                branchRepository.count(),
                screenRepository.count(),
                showtimeRepository.count(),
                reservationRepository.count(),
                reservationRepository.findByReservationStatus(ReservationStatus.CONFIRMED).size(),
                reservationRepository.findByReservationStatus(ReservationStatus.PENDING).size());
    }
}