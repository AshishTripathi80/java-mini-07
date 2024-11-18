package com.seat_booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentRequest {

    private Long bookingId;

    private Double paymentAmount;

}
