package com.se2030.cinema.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se2030.cinema.dto.seat.SeatAdminRequest;
import com.se2030.cinema.dto.seat.SeatAdminResponse;
import com.se2030.cinema.entity.Screen;
import com.se2030.cinema.entity.Seat;
import com.se2030.cinema.exception.ResourceNotFoundException;
import com.se2030.cinema.repository.ScreenRepository;
import com.se2030.cinema.repository.SeatRepository;
import com.se2030.cinema.service.SeatService;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;

    public SeatServiceImpl(SeatRepository seatRepository, ScreenRepository screenRepository) {
        this.seatRepository = seatRepository;
        this.screenRepository = screenRepository;
    }

    @Override
    @Transactional
    public SeatAdminResponse create(SeatAdminRequest request) {
        Seat seat = new Seat();
        apply(seat, request);
        return toResponse(seatRepository.save(seat));
    }

    @Override
    @Transactional
    public SeatAdminResponse update(Long id, SeatAdminRequest request) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        apply(seat, request);
        return toResponse(seatRepository.save(seat));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        seat.setActive(Boolean.FALSE);
        seatRepository.save(seat);
    }

    @Override
    public List<SeatAdminResponse> list(Long screenId) {
        List<Seat> seats = screenId == null ? seatRepository.findAll() : seatRepository.findByScreenId(screenId);
        return seats.stream().map(this::toResponse).toList();
    }

    private void apply(Seat seat, SeatAdminRequest request) {
        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));
        seat.setScreen(screen);
        seat.setRowName(request.rowName().trim().toUpperCase());
        seat.setSeatNumber(request.seatNumber());
        seat.setSeatType(request.seatType());
        seat.setActive(request.active() == null || request.active());
    }

    private SeatAdminResponse toResponse(Seat seat) {
        return new SeatAdminResponse(seat.getId(), seat.getScreen().getId(), seat.getScreen().getScreenName(),
                seat.getRowName(), seat.getSeatNumber(), seat.getSeatType().name(), seat.getActive());
    }
}