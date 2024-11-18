package com.payment_service.service;

import com.payment_service.entity.Payment;

public interface PaymentService {

    Payment processPayment(Payment payment);
}
