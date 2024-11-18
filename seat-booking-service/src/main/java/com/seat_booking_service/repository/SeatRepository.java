package com.seat_booking_service.repository;

import com.seat_booking_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,Long> {

    List<Seat> findByIsBookedFalse();
    List<Seat> findBySeatCodeIn(List<String> seatCodes);

}
