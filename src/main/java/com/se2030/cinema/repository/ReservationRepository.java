package com.se2030.cinema.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.se2030.cinema.entity.Reservation;
import com.se2030.cinema.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

        boolean existsByUserId(Long userId);

    Optional<Reservation> findByBookingReference(String bookingReference);

    List<Reservation> findByUserIdOrderByBookingDateDesc(Long userId);

    List<Reservation> findByReservationStatus(ReservationStatus reservationStatus);

    @Query("""
            select r from Reservation r
            join r.user u
            where lower(r.bookingReference) like lower(concat('%', :term, '%'))
               or lower(u.fullName) like lower(concat('%', :term, '%'))
               or lower(u.email) like lower(concat('%', :term, '%'))
            order by r.bookingDate desc
            """)
    List<Reservation> searchReservations(@Param("term") String term);

    @Query("""
            select r from Reservation r
            where date(r.bookingDate) between :fromDate and :toDate
            order by r.bookingDate desc
            """)
    List<Reservation> findByBookingDateBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}