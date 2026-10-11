package com.se2030.cinema.dto.seat;

import java.time.LocalDateTime;
import java.util.List;

public record SeatHoldResponse(Long showtimeId, List<Long> seatIds, LocalDateTime expiresAt) {
}