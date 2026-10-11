package com.se2030.cinema.service;

import java.util.List;

import com.se2030.cinema.dto.branch.CinemaBranchRequest;
import com.se2030.cinema.dto.branch.CinemaBranchResponse;

public interface CinemaBranchService {

    CinemaBranchResponse create(CinemaBranchRequest request);

    CinemaBranchResponse update(Long id, CinemaBranchRequest request);

    void deactivate(Long id);

    CinemaBranchResponse getById(Long id);

    List<CinemaBranchResponse> listAll();
}