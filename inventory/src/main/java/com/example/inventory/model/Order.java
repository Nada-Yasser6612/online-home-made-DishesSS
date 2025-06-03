package com.example.inventory.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Data
@EqualsAndHashCode(exclude = "orderDishes")
@ToString(exclude = "orderDishes")
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "shipping_amount")
    private Double shippingAmount;

    @Column(nullable = false)
    private String status = "PENDING";

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<OrderDish> orderDishes = new HashSet<>();

    // Helper method to manage bidirectional relationship
    public void addOrderDish(OrderDish orderDish) {
        orderDishes.add(orderDish);
        orderDish.setOrder(this);
    }

    public void removeOrderDish(OrderDish orderDish) {
        orderDishes.remove(orderDish);
        orderDish.setOrder(null);
    }
} 