package com.seat_booking_service.repository;

import com.seat_booking_service.entity.Booking;
import com.seat_booking_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByUser(User user);
}