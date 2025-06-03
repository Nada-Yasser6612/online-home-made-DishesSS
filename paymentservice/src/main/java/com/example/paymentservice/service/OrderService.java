package com.example.paymentservice.service;

import com.example.inventory.dto.OrderValidationRequest;
import com.example.inventory.dto.OrderValidationResponse;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.messaging.events.PaymentFailedEvent;
import com.example.paymentservice.messaging.PaymentEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class OrderService {

    private final RabbitTemplate rabbitTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher eventPublisher;
    private final FailedPaymentService failedPaymentService; // ✅ inject the service
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());

    public OrderService(RabbitTemplate rabbitTemplate,
                        PaymentRepository paymentRepository,
                        PaymentEventPublisher eventPublisher,
                        FailedPaymentService failedPaymentService) {
        this.rabbitTemplate = rabbitTemplate;
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
        this.failedPaymentService = failedPaymentService; // ✅ assign
    }

    @Transactional
    public OrderValidationResponse validateOrder(OrderValidationRequest request) {
        LOGGER.info("📤 Sending order validation request for orderId: " + request.getOrderId());

        Object response = rabbitTemplate.convertSendAndReceive(
                "order.direct.exchange",
                "order.validation.request",
                request
        );

        if (response == null || !(response instanceof OrderValidationResponse)) {
            LOGGER.warning("❌ No valid response received. Inventory service might be down.");
            return null;
        }

        OrderValidationResponse validationResponse = (OrderValidationResponse) response;

        LOGGER.info("📥 Received response for orderId: " + validationResponse.getOrderId() +
                " | Valid: " + validationResponse.isValid() +
                " | Reason: " + validationResponse.getReason());

        if (validationResponse.isValid()) {
            Payment payment = new Payment();
            payment.setOrderId(validationResponse.getOrderId());
            payment.setStatus("CONFIRMED");
            paymentRepository.save(payment);

            LOGGER.info("💳 Payment created for orderId: " + validationResponse.getOrderId());
        } else {
            // ❌ Save payment with FAILED status
            Payment failedPayment = new Payment();
            failedPayment.setOrderId(validationResponse.getOrderId());
            failedPayment.setStatus("FAILED");
            paymentRepository.save(failedPayment);

            // 🔎 Add detailed reason
            String detailedReason = "Order #" + validationResponse.getOrderId() + " rejected: Not enough minimum charge" ;

            // ✅ Save failed payment info
            failedPaymentService.saveFailedPayment(failedPayment, detailedReason);

            // 📣 Publish failure event
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    validationResponse.getOrderId(),
                    "REJECTED",
                    detailedReason
            );
            eventPublisher.publishPaymentFailed(failedEvent);
        }

        return validationResponse;
    }
}
