package com.example.paymentservice.service;

import com.example.paymentservice.model.FailedPayment;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.FailedPaymentRepository;
import org.springframework.stereotype.Service;
@Service
public class FailedPaymentService
{

    private final FailedPaymentRepository repository;

    public FailedPaymentService(FailedPaymentRepository repository) {
        this.repository = repository;
    }


    public void saveFailedPayment(Payment payment, String reason) {
        FailedPayment failed = new FailedPayment();
        failed.setPayment(payment);
        failed.setReason(reason);
        repository.save(failed); // ✅ use the instance field, not the class name
    }
}
