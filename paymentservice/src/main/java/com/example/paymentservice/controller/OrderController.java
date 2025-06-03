package com.example.paymentservice.controller;

import com.example.inventory.dto.*;
import com.example.paymentservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/validate")
    public ResponseEntity<?> processOrder(@RequestBody OrderValidationRequest request) {
        OrderValidationResponse response = orderService.validateOrder(request);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("No response from inventory.");
        } else if (!response.isValid()) {
            return ResponseEntity.badRequest().body(response.getReason());
        }

        return ResponseEntity.ok("Order confirmed and proceeding to payment.");
    }
}
