package com.example.inventory.repository;

import com.example.inventory.model.OrderDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDishRepository extends JpaRepository<OrderDish, Long> {

    @Query("SELECT od FROM OrderDish od JOIN FETCH od.dish WHERE od.order.id = :orderId")
    List<OrderDish> findWithDishByOrderId(@Param("orderId") Long orderId); // ✅ FIXED to use Long
}
