package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderValidationResponse {
    private int orderId;
    private boolean valid;
    private String reason;
    public String getReason() {
        return reason;
    }

    public boolean isValid() {
        return valid;
    }

    public int getOrderId() {
        return orderId;
    }
}
