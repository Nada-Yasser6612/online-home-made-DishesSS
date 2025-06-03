package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
// in com.example.dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderValidationRequest {
    private int orderId;
    private List<OrderItemRequest> items;

    public int getOrderId() {
        return orderId;
    }
}

