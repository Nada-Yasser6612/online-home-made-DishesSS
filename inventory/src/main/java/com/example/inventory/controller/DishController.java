package com.example.inventory.controller;

import com.example.inventory.model.Dish;
import com.example.inventory.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public ResponseEntity<?> addDishPost(
            @RequestParam Integer repId,
            @RequestParam String dishName,
            @RequestParam Double price,
            @RequestParam Integer amount) {
        try {
            Dish dish = dishService.addDish(repId, dishName, price, amount);
            return ResponseEntity.ok(dish);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal server error"));
        }
    }


    @GetMapping("/rep/{repId}")
    public ResponseEntity<List<Dish>> listDishesByRepId(@PathVariable Integer repId) {
        return ResponseEntity.ok(dishService.listDishesByRepId(repId));
    }

    @PutMapping("/{dishId}")
    public ResponseEntity<Dish> updateDishInfo(
            @PathVariable Long dishId,
            @RequestParam(required = false) Integer amount,
            @RequestParam(required = false) Double price) {
        return ResponseEntity.ok(dishService.updateDishInfo(dishId, amount, price));
    }

    @GetMapping("/rep/{repId}/sold")
    public ResponseEntity<List<String>> getSoldDishesByRepId(@PathVariable Integer repId) {
        return ResponseEntity.ok(dishService.getDummySoldDishesByRepId(repId));
    }

}