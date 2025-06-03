package com.example.inventory.dto;

import lombok.Data;

@Data
public class DishSalesSummary {
    private Long dishId;
    private String dishName;
    private Integer totalQuantity;
    private Double totalRevenue;
    private Double averagePrice;
} 