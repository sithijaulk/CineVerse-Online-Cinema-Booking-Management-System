package com.se2030.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se2030.cinema.entity.CinemaBranch;

public interface CinemaBranchRepository extends JpaRepository<CinemaBranch, Long> {

    boolean existsByBranchNameIgnoreCase(String branchName);
}