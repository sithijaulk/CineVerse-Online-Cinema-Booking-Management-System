package com.se2030.cinema.service;

import java.util.List;

import com.se2030.cinema.dto.seat.SeatAdminRequest;
import com.se2030.cinema.dto.seat.SeatAdminResponse;

public interface SeatService {

    SeatAdminResponse create(SeatAdminRequest request);

    SeatAdminResponse update(Long id, SeatAdminRequest request);

    void deactivate(Long id);

    List<SeatAdminResponse> list(Long screenId);
}