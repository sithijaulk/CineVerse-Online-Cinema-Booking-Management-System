package com.se2030.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se2030.cinema.entity.Screen;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByBranchId(Long branchId);
}