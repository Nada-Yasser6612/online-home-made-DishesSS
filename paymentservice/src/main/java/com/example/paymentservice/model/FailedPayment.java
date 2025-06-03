package com.example.paymentservice.model;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "failed_payments")
@Data
public class FailedPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Correct usage of @ManyToOne with @JoinColumn
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "reason", nullable = false)
    private String reason;

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
