package com.seat_booking_service.controller;

import com.seat_booking_service.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping
    public ResponseEntity<List<?>> getAllAvailableSeat() {
        return ResponseEntity.ok(seatService.getAllAvailableSeat());
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookSeats(@RequestParam Long userId, @RequestBody List<String> seatCodes) {
        return seatService.bookAndPayForSeats(userId, seatCodes);
    }
}
