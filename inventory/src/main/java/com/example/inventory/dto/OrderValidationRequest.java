package com.example.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
// in com.example.dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderValidationRequest {
    @JsonProperty("orderId")
    private int orderId;

    @JsonProperty("items")
    private List<OrderItemRequest> items;
    public int getOrderId() {
        return orderId;
    }
}

