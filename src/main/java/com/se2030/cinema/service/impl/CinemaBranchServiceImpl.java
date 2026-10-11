package com.se2030.cinema.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se2030.cinema.dto.branch.CinemaBranchRequest;
import com.se2030.cinema.dto.branch.CinemaBranchResponse;
import com.se2030.cinema.entity.CinemaBranch;
import com.se2030.cinema.exception.BusinessRuleException;
import com.se2030.cinema.exception.ResourceNotFoundException;
import com.se2030.cinema.repository.CinemaBranchRepository;
import com.se2030.cinema.service.CinemaBranchService;

@Service
public class CinemaBranchServiceImpl implements CinemaBranchService {

    private final CinemaBranchRepository branchRepository;

    public CinemaBranchServiceImpl(CinemaBranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public CinemaBranchResponse create(CinemaBranchRequest request) {
        if (branchRepository.existsByBranchNameIgnoreCase(request.branchName())) {
            throw new BusinessRuleException("Branch name already exists");
        }
        CinemaBranch branch = new CinemaBranch();
        apply(branch, request);
        return toResponse(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public CinemaBranchResponse update(Long id, CinemaBranchRequest request) {
        CinemaBranch branch = getEntity(id);
        apply(branch, request);
        return toResponse(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        CinemaBranch branch = getEntity(id);
        branch.setActive(Boolean.FALSE);
        branchRepository.save(branch);
    }

    @Override
    public CinemaBranchResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    public List<CinemaBranchResponse> listAll() {
        return branchRepository.findAll().stream().map(this::toResponse).toList();
    }

    private CinemaBranch getEntity(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
    }

    private void apply(CinemaBranch branch, CinemaBranchRequest request) {
        branch.setBranchName(request.branchName().trim());
        branch.setLocation(request.location().trim());
        branch.setActive(request.active() == null || request.active());
    }

    private CinemaBranchResponse toResponse(CinemaBranch branch) {
        return new CinemaBranchResponse(branch.getId(), branch.getBranchName(), branch.getLocation(), branch.getActive());
    }
}