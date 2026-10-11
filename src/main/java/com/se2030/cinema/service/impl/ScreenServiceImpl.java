package com.se2030.cinema.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se2030.cinema.dto.screen.ScreenRequest;
import com.se2030.cinema.dto.screen.ScreenResponse;
import com.se2030.cinema.entity.CinemaBranch;
import com.se2030.cinema.entity.Screen;
import com.se2030.cinema.entity.Seat;
import com.se2030.cinema.entity.SeatType;
import com.se2030.cinema.exception.ResourceNotFoundException;
import com.se2030.cinema.repository.CinemaBranchRepository;
import com.se2030.cinema.repository.ScreenRepository;
import com.se2030.cinema.repository.SeatRepository;
import com.se2030.cinema.service.ScreenService;

@Service
public class ScreenServiceImpl implements ScreenService {

    private final ScreenRepository screenRepository;
    private final CinemaBranchRepository branchRepository;
    private final SeatRepository seatRepository;

    public ScreenServiceImpl(ScreenRepository screenRepository, CinemaBranchRepository branchRepository,
            SeatRepository seatRepository) {
        this.screenRepository = screenRepository;
        this.branchRepository = branchRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional
    public ScreenResponse create(ScreenRequest request) {
        CinemaBranch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        Screen screen = new Screen();
        screen.setBranch(branch);
        screen.setScreenName(request.screenName().trim());
        screen.setCapacity(request.capacity());
        screen.setActive(request.active() == null || request.active());
        Screen saved = screenRepository.save(screen);
        generateSeats(saved);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ScreenResponse update(Long id, ScreenRequest request) {
        Screen screen = getEntity(id);
        CinemaBranch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        screen.setBranch(branch);
        screen.setScreenName(request.screenName().trim());
        screen.setCapacity(request.capacity());
        screen.setActive(request.active() == null || request.active());
        return toResponse(screenRepository.save(screen));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Screen screen = getEntity(id);
        screen.setActive(Boolean.FALSE);
        screenRepository.save(screen);
    }

    @Override
    public ScreenResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    public List<ScreenResponse> listAll() {
        return screenRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<ScreenResponse> listByBranch(Long branchId) {
        return screenRepository.findByBranchId(branchId).stream().map(this::toResponse).toList();
    }

    private Screen getEntity(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));
    }

    private void generateSeats(Screen screen) {
        if (!seatRepository.findByScreenId(screen.getId()).isEmpty()) {
            return;
        }

        List<Seat> seats = new ArrayList<>();
        int seatsPerRow = 10;
        int rows = (int) Math.ceil((double) screen.getCapacity() / seatsPerRow);

        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            String rowName = String.valueOf((char) ('A' + rowIndex));
            for (int seatNumber = 1; seatNumber <= seatsPerRow && seats.size() < screen.getCapacity(); seatNumber++) {
                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowName(rowName);
                seat.setSeatNumber(seatNumber);
                seat.setSeatType(seatNumber > 7 ? SeatType.PREMIUM : SeatType.STANDARD);
                seat.setActive(Boolean.TRUE);
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }

    private ScreenResponse toResponse(Screen screen) {
        return new ScreenResponse(screen.getId(), screen.getBranch().getId(), screen.getBranch().getBranchName(),
                screen.getScreenName(), screen.getCapacity(), screen.getActive());
    }
}