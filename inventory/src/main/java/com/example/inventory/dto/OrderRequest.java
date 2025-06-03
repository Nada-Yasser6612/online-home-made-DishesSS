package com.example.inventory.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private List<DishQuantity> dishQuantities;
    private Integer customerId;

    @Data
    public static class DishQuantity {
        private Long dishId;
        private Integer quantity;
    }
} 