package com.example.inventory.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@EqualsAndHashCode(exclude = "orderDishes")
@ToString(exclude = "orderDishes")
@Table(name = "dishes")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rep_id")
    private Integer repId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "price")
    private Double price;

    @Column(name = "dish_name")
    private String dishName;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<OrderDish> orderDishes = new HashSet<>();

    // Helper method to manage bidirectional relationship
    public void addOrderDish(OrderDish orderDish) {
        orderDishes.add(orderDish);
        orderDish.setDish(this);
    }

    public void removeOrderDish(OrderDish orderDish) {
        orderDishes.remove(orderDish);
        orderDish.setDish(null);
    }
} 