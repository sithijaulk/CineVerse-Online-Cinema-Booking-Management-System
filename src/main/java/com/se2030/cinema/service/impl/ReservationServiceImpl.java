package com.se2030.cinema.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se2030.cinema.config.AppProperties;
import com.se2030.cinema.dto.reservation.BookingRequest;
import com.se2030.cinema.dto.reservation.ReservationResponse;
import com.se2030.cinema.dto.seat.SeatHoldRequest;
import com.se2030.cinema.dto.seat.SeatHoldResponse;
import com.se2030.cinema.entity.Payment;
import com.se2030.cinema.entity.PaymentStatus;
import com.se2030.cinema.entity.Reservation;
import com.se2030.cinema.entity.ReservationSeat;
import com.se2030.cinema.entity.ReservationStatus;
import com.se2030.cinema.entity.Seat;
import com.se2030.cinema.entity.SeatHold;
import com.se2030.cinema.entity.Showtime;
import com.se2030.cinema.entity.User;
import com.se2030.cinema.exception.BusinessRuleException;
import com.se2030.cinema.exception.ResourceNotFoundException;
import com.se2030.cinema.repository.PaymentRepository;
import com.se2030.cinema.repository.ReservationRepository;
import com.se2030.cinema.repository.ReservationSeatRepository;
import com.se2030.cinema.repository.SeatRepository;
import com.se2030.cinema.repository.SeatHoldRepository;
import com.se2030.cinema.repository.ShowtimeRepository;
import com.se2030.cinema.repository.UserRepository;
import com.se2030.cinema.service.ReservationService;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final AppProperties appProperties;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
            ReservationSeatRepository reservationSeatRepository, ShowtimeRepository showtimeRepository,
            SeatRepository seatRepository, SeatHoldRepository seatHoldRepository, UserRepository userRepository, PaymentRepository paymentRepository,
            AppProperties appProperties) {
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.appProperties = appProperties;
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(Long userId, BookingRequest request) {
        cleanupExpiredHolds();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Showtime showtime = showtimeRepository.findById(request.showtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        if (showtime.getStatus() != com.se2030.cinema.entity.ShowtimeStatus.SCHEDULED
            && showtime.getStatus() != com.se2030.cinema.entity.ShowtimeStatus.RESCHEDULED) {
            throw new BusinessRuleException("This showtime is not available for booking");
        }

        if (request.seatIds() == null || request.seatIds().isEmpty()) {
            throw new BusinessRuleException("Select at least one seat");
        }

        List<Seat> seats = seatRepository.findAllById(request.seatIds());
        if (seats.size() != request.seatIds().size()) {
            throw new BusinessRuleException("One or more seats were not found");
        }

        for (Seat seat : seats) {
            if (!seat.getScreen().getId().equals(showtime.getScreen().getId())) {
                throw new BusinessRuleException("Selected seat does not belong to the chosen screen");
            }
                if (reservationSeatRepository.existsByShowtimeIdAndSeatIdAndReservationReservationStatus(
                    showtime.getId(), seat.getId(), ReservationStatus.CONFIRMED)) {
                throw new BusinessRuleException("One or more selected seats are no longer available");
            }
            boolean ownedHold = seatHoldRepository.findByUserIdAndShowtimeIdAndExpiresAtAfter(userId, showtime.getId(), LocalDateTime.now())
                    .stream().anyMatch(hold -> hold.getSeat().getId().equals(seat.getId()));
            if (!ownedHold) {
                throw new BusinessRuleException("Your seat hold has expired. Please select the seats again.");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setBookingReference("P-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        reservation.setUser(user);
        reservation.setShowtime(showtime);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setBookingDate(LocalDateTime.now());
        reservation.setSeatSummary(formatSeatSummary(seats));
        reservation.setTotalAmount(showtime.getTicketPrice().multiply(BigDecimal.valueOf(seats.size())));
        reservation = reservationRepository.save(reservation);
        reservation.setBookingReference(String.format("LCO-2026-%06d", reservation.getId()));
        reservation = reservationRepository.save(reservation);

        List<ReservationSeat> reservationSeats = new ArrayList<>();
        for (Seat seat : seats) {
            ReservationSeat reservationSeat = new ReservationSeat();
            reservationSeat.setReservation(reservation);
            reservationSeat.setShowtime(showtime);
            reservationSeat.setSeat(seat);
            reservationSeats.add(reservationSeat);
        }
        try {
            reservationSeatRepository.saveAllAndFlush(reservationSeats);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("One or more selected seats were just booked by another customer. Please choose different seats.");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(reservation.getTotalAmount());
        payment.setPaymentMethod(request.paymentMethod());
        boolean paymentSuccess = request.simulateSuccess() == null || request.simulateSuccess();
        payment.setPaymentStatus(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setTransactionRef("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        paymentRepository.save(payment);

        reservation.setPayment(payment);
        if (paymentSuccess) {
            reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        } else {
            reservation.setReservationStatus(ReservationStatus.CANCELLED);
            reservationSeatRepository.deleteAllInBatch(reservationSeats);
        }
        seatHoldRepository.deleteAll(seatHoldRepository.findByUserIdAndShowtimeId(userId, showtime.getId()));
        seatHoldRepository.flush();
        reservation = reservationRepository.save(reservation);
        return toResponse(reservation);
    }

    @Override
    @Transactional
    public SeatHoldResponse holdSeats(Long userId, SeatHoldRequest request) {
        cleanupExpiredHolds();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Showtime showtime = showtimeRepository.findById(request.showtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        if (request.seatIds() == null || request.seatIds().isEmpty()) {
            throw new BusinessRuleException("Select at least one seat");
        }

        List<Long> distinctSeatIds = request.seatIds().stream().distinct().toList();
        List<Seat> seats = seatRepository.findAllById(distinctSeatIds);
        if (seats.size() != distinctSeatIds.size()) {
            throw new BusinessRuleException("One or more seats were not found");
        }

        seatHoldRepository.deleteAll(seatHoldRepository.findByUserIdAndShowtimeId(userId, showtime.getId()));
        LocalDateTime now = LocalDateTime.now();
        for (Seat seat : seats) {
            if (!seat.getScreen().getId().equals(showtime.getScreen().getId())) {
                throw new BusinessRuleException("Selected seat does not belong to the chosen screen");
            }
                if (reservationSeatRepository.existsByShowtimeIdAndSeatIdAndReservationReservationStatus(
                    showtime.getId(), seat.getId(), ReservationStatus.CONFIRMED)
                    || seatHoldRepository.existsByShowtimeIdAndSeatIdAndExpiresAtAfter(showtime.getId(), seat.getId(), now)) {
                throw new BusinessRuleException("One or more selected seats are no longer available");
            }
        }

        LocalDateTime expiresAt = now.plusMinutes(appProperties.getHoldMinutes());
        List<SeatHold> holds = seats.stream().map(seat -> {
            SeatHold hold = new SeatHold();
            hold.setShowtime(showtime);
            hold.setSeat(seat);
            hold.setUser(user);
            hold.setExpiresAt(expiresAt);
            return hold;
        }).toList();
        try {
            seatHoldRepository.saveAllAndFlush(holds);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("One or more selected seats were just held by another customer");
        }
        return new SeatHoldResponse(showtime.getId(), distinctSeatIds, expiresAt);
    }

    @Override
    @Transactional
    public void releaseSeats(Long userId, Long showtimeId) {
        seatHoldRepository.deleteAll(seatHoldRepository.findByUserIdAndShowtimeId(userId, showtimeId));
    }

    @Override
    public ReservationResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    public ReservationResponse getByIdForUser(Long id, Long userId) {
        Reservation reservation = getEntity(id);
        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessRuleException("You are not allowed to view this booking");
        }
        return toResponse(reservation);
    }

    @Override
    public ReservationResponse getByBookingReference(String bookingReference) {
        return toResponse(reservationRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found")));
    }

    @Override
    public List<ReservationResponse> getMyBookings(Long userId) {
        return reservationRepository.findByUserIdOrderByBookingDateDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<ReservationResponse> search(String term) {
        List<Reservation> reservations = term == null || term.isBlank()
            ? reservationRepository.findAll()
            : reservationRepository.searchReservations(term.trim());
        return reservations.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void cancel(Long reservationId) {
        Reservation reservation = getEntity(reservationId);
        cancelEntity(reservation);
    }

    @Override
    @Transactional
    public void cancelForUser(Long reservationId, Long userId) {
        Reservation reservation = getEntity(reservationId);
        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessRuleException("You are not allowed to cancel this booking");
        }
        cancelEntity(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse verifyByReference(String bookingReference, Long staffUserId) {
        Reservation reservation = reservationRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (reservation.getReservationStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessRuleException("Only confirmed bookings can be verified");
        }
        reservation.setVerifiedAt(LocalDateTime.now());
        reservation.setVerifiedBy(staffUserId);
        return toResponse(reservationRepository.save(reservation));
    }

    private void cancelEntity(Reservation reservation) {
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleException("This booking is already cancelled");
        }
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        reservationSeatRepository.deleteAllInBatch(reservationSeatRepository.findByReservationId(reservation.getId()));
    }

    @Override
    @Scheduled(fixedDelayString = "PT5M")
    @Transactional
    public void cleanupExpiredHolds() {
        seatHoldRepository.deleteAllInBatch(seatHoldRepository.findByExpiresAtBefore(LocalDateTime.now()));
    }

    private Reservation getEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    private String formatSeatSummary(List<Seat> seats) {
        return seats.stream()
                .sorted(Comparator.comparing(Seat::getRowName).thenComparing(Seat::getSeatNumber))
                .map(seat -> seat.getRowName() + seat.getSeatNumber())
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }

    private ReservationResponse toResponse(Reservation reservation) {
        Payment payment = paymentRepository.findByReservationId(reservation.getId()).orElse(null);
        return new ReservationResponse(
                reservation.getId(),
                reservation.getBookingReference(),
                reservation.getUser().getFullName(),
                reservation.getUser().getEmail(),
                reservation.getShowtime().getMovie().getTitle(),
                reservation.getShowtime().getScreen().getBranch().getBranchName(),
                reservation.getShowtime().getScreen().getScreenName(),
                reservation.getShowtime().getShowDate(),
                reservation.getShowtime().getStartTime(),
                reservation.getSeatSummary(),
                reservation.getTotalAmount(),
                reservation.getReservationStatus(),
                payment == null ? PaymentStatus.PENDING : payment.getPaymentStatus(),
                payment == null ? null : payment.getPaymentMethod(),
                reservation.getBookingDate(),
                reservation.getVerifiedAt() != null);
    }
}