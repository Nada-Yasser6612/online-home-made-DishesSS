package com.example.inventory.repository;

import com.example.inventory.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    // Get all dishes created by a representative (by rep_id)
    List<Dish> findByRepId(Integer repId);

    // Get only dishes with amount > 0 for a representative
    @Query("SELECT d FROM Dish d WHERE d.repId = :repId AND d.amount > 0")
    List<Dish> findAvailableDishesByRepId(Integer repId);


    // Get sold dishes by actual rep_id (used in dishes table)
    @Query("SELECT DISTINCT d FROM Dish d JOIN d.orderDishes od " +
            "WHERE d.repId = :repId AND od.order.status = 'CONFIRMED'")
    List<Dish> findSoldDishesByRepId(Integer repId);

    // Ensure no duplicate dish name per representative
    Optional<Dish> findByRepIdAndDishName(Integer repId, String dishName);
}
