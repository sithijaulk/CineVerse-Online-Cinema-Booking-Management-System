package com.se2030.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinemaReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaReservationApplication.class, args);
    }
}