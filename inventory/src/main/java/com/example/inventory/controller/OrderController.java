package com.example.inventory.controller;

import com.example.inventory.dto.OrderRequest;
import com.example.inventory.dto.DishSalesSummary;
import com.example.inventory.dto.OrderStatusUpdate;
import com.example.inventory.model.Order;
import com.example.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> makeOrder(@RequestBody OrderRequest request) {
        try {
            Order order = orderService.makeOrder(request.getDishQuantities(), request.getCustomerId());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            // Handle known runtime exceptions (e.g., stock issues, dish not found)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal server error"));
        }
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> listPastOrdersByCustomerId(@PathVariable Integer customerId) {
        List<Order> orders = orderService.listPastOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/representative/{repId}")
    public ResponseEntity<List<Order>> listOrdersByRepresentativeId(@PathVariable Integer repId) {
        List<Order> orders = orderService.listOrdersByRepresentativeId(repId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/representative/{repId}/summary")
    public ResponseEntity<List<DishSalesSummary>> getDishSalesSummaryByRepresentativeId(@PathVariable Integer repId) {
        List<DishSalesSummary> summary = orderService.getDishSalesSummaryByRepresentativeId(repId);
        return ResponseEntity.ok(summary);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdate statusUpdate) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, statusUpdate.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
} 