package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private Long dishId;
    private int quantity;

    public Long getDishId() {
        return dishId;
    }

    public int getQuantity() {
        return quantity;
    }
}
