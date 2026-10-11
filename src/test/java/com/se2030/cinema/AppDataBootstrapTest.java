package com.se2030.cinema;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.se2030.cinema.repository.CinemaBranchRepository;
import com.se2030.cinema.repository.MovieRepository;
import com.se2030.cinema.repository.UserRepository;

@SpringBootTest
class AppDataBootstrapTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaBranchRepository cinemaBranchRepository;

    @Test
    void appStartsWithSeedAdminAndSampleData() {
        assertThat(userRepository.existsByEmail("admin@cinema.com")).isTrue();
        assertThat(movieRepository.count()).isGreaterThan(0);
        assertThat(cinemaBranchRepository.count()).isGreaterThan(0);
    }
}
