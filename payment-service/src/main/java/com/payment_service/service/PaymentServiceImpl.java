package com.payment_service.service;

import com.payment_service.entity.Payment;
import com.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(Payment payment) {
        // Simulate payment processing logic, e.g., communicating with a payment gateway
        payment.setPaymentStatus("SUCCESS"); // or "FAILED" based on processing result
        return paymentRepository.save(payment);
    }
}
