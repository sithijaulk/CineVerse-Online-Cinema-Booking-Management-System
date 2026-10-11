package com.se2030.cinema.service;

import java.util.List;

import com.se2030.cinema.dto.screen.ScreenRequest;
import com.se2030.cinema.dto.screen.ScreenResponse;

public interface ScreenService {

    ScreenResponse create(ScreenRequest request);

    ScreenResponse update(Long id, ScreenRequest request);

    void deactivate(Long id);

    ScreenResponse getById(Long id);

    List<ScreenResponse> listAll();

    List<ScreenResponse> listByBranch(Long branchId);
}