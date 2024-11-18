package com.seat_booking_service.service;

import com.seat_booking_service.entity.Seat;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SeatService {

    List<Seat> getAllAvailableSeat();

    ResponseEntity<String> bookAndPayForSeats(Long userId, List<String> seatCodes);


}
