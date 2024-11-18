package com.payment_service.controller;


import com.payment_service.entity.Payment;
import com.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<String> makePayment(@RequestBody Payment payment) {
        payment.setPaymentTime(LocalDateTime.now());
        Payment savedPayment = paymentService.processPayment(payment);

        if ("SUCCESS".equals(savedPayment.getPaymentStatus())) {
            return ResponseEntity.ok("Payment successful.");
        } else {
            return ResponseEntity.status(500).body("Payment failed.");
        }
    }
}
