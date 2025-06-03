package com.example.paymentservice.repository;

import com.example.paymentservice.model.FailedPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedPaymentRepository extends JpaRepository<FailedPayment, Long> {
}
