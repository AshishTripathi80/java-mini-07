package com.seat_booking_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorResponse {
    private String message;
    private int code;
    private String timestamp;

    public ErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("d'th' MMM yyyy HH:mm:ss"));
    }

}
