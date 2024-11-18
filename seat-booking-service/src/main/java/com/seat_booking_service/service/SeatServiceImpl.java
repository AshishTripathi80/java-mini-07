package com.seat_booking_service.service;

import com.seat_booking_service.dto.PaymentRequest;
import com.seat_booking_service.entity.Booking;
import com.seat_booking_service.entity.Seat;
import com.seat_booking_service.entity.User;
import com.seat_booking_service.exception.BookingException;
import com.seat_booking_service.exception.PaymentException;
import com.seat_booking_service.repository.BookingRepository;
import com.seat_booking_service.repository.SeatRepository;
import com.seat_booking_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Seat> getAllAvailableSeat() {
        return seatRepository.findByIsBookedFalse();
    }

    @Override
    public ResponseEntity<String> bookAndPayForSeats(Long userId, List<String> seatCodes) {
        try {
            String temporaryBookingResult = bookSeats(userId, seatCodes);
            if (!temporaryBookingResult.contains("Temporary booking created")) {
                throw new BookingException(temporaryBookingResult);
            }

            String bookingId = temporaryBookingResult.split(": ")[1];

            // Call payment-service
            PaymentRequest paymentRequest = new PaymentRequest(Long.parseLong(bookingId), calculatePaymentAmount(seatCodes));
            ResponseEntity<String> paymentResponse = restTemplate.postForEntity("http://localhost:8081/api/payment/process", paymentRequest, String.class);

            if (paymentResponse.getStatusCode().is2xxSuccessful()) {
                finalizeBooking(Long.parseLong(bookingId));
                return ResponseEntity.ok("Seats booked and payment successful.");
            } else {
                rollbackBooking(Long.parseLong(bookingId));
                throw new PaymentException("Payment failed. Booking rolled back.");
            }
        } catch (Exception ex) {
            throw new PaymentException("Payment service is unavailable. Booking rolled back.");
        }
    }

    public String bookSeats(Long userId, List<String> seatCodes) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BookingException("User not found.");
        }

        User user = userOptional.get();

        // Check seat limit
        List<Booking> existingBookings = bookingRepository.findByUser(user);
        int totalBookedSeats = existingBookings.stream()
                .mapToInt(booking -> booking.getSeats().size())
                .sum();

        if (totalBookedSeats + seatCodes.size() > 8) {
            throw new BookingException("User has already reached the maximum booking limit of 8 seats.");
        }

        // Fetch seats
        List<Seat> seatsToBook = seatRepository.findBySeatCodeIn(seatCodes);
        if (seatsToBook.size() != seatCodes.size()) {
            throw new BookingException("One or more seat codes provided are invalid.");
        }

        for (Seat seat : seatsToBook) {
            if (seat.getIsBooked()) {
                throw new BookingException("Some of the requested seats are already booked.");
            }
        }

        // Temporarily reserve seats
        seatsToBook.forEach(seat -> seat.setIsBooked(true));
        seatRepository.saveAll(seatsToBook);

        // Create temporary booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSeats(seatsToBook);
        booking.setBookingDate(new Date());
        Booking savedBooking = bookingRepository.save(booking);

        return "Temporary booking created with ID: " + savedBooking.getId();
    }

    public void finalizeBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setFinalized(true); // Mark as finalized
            bookingRepository.save(booking); // Persist the change
        }
    }

    public void rollbackBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            List<Seat> seats = booking.getSeats();
            seats.forEach(seat -> seat.setIsBooked(false)); // Release seats
            seatRepository.saveAll(seats);
            bookingRepository.delete(booking); // Delete the booking
        }
    }

    private double calculatePaymentAmount(List<String> seatCodes) {
        return seatCodes.size() * 100; // Example calculation
    }
}
