package com.se2030.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se2030.cinema.dto.branch.CinemaBranchRequest;
import com.se2030.cinema.dto.branch.CinemaBranchResponse;
import com.se2030.cinema.service.CinemaBranchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/branches")
public class AdminBranchController {

    private final CinemaBranchService branchService;

    public AdminBranchController(CinemaBranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public ResponseEntity<List<CinemaBranchResponse>> list() {
        return ResponseEntity.ok(branchService.listAll());
    }

    @PostMapping
    public ResponseEntity<CinemaBranchResponse> create(@Valid @RequestBody CinemaBranchRequest request) {
        return ResponseEntity.ok(branchService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CinemaBranchResponse> update(@PathVariable Long id,
            @Valid @RequestBody CinemaBranchRequest request) {
        return ResponseEntity.ok(branchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}