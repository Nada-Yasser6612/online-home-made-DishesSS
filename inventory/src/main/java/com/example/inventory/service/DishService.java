package com.example.inventory.service;

import com.example.inventory.model.Dish;
import com.example.inventory.model.OrderDish;
import com.example.inventory.repository.DishRepository;
import com.example.inventory.repository.OrderDishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class DishService {

    private static final Logger logger = LoggerFactory.getLogger(DishService.class);

    @Autowired
    private DishRepository dishRepository;

    // ✅ Inject the missing repository here
    @Autowired
    private OrderDishRepository orderDishRepository;

    @Transactional
    public Dish addDish(Integer repId, String dishName, Double price, Integer amount) {
        logger.info("Adding new dish: repId={}, dishName={}, price={}, amount={}", repId, dishName, price, amount);

        boolean exists = dishRepository.findByRepIdAndDishName(repId, dishName).isPresent();
        if (exists) {
            throw new IllegalArgumentException("Dish name '" + dishName + "' already exists for this representative.");
        }

        Dish dish = new Dish();
        dish.setRepId(repId);
        dish.setDishName(dishName);
        dish.setPrice(price);
        dish.setAmount(amount);

        Dish savedDish = dishRepository.save(dish);
        logger.info("Saved dish with ID: {}", savedDish.getId());

        return dishRepository.findById(savedDish.getId())
                .orElseThrow(() -> {
                    logger.error("Failed to save dish - could not retrieve after save");
                    return new RuntimeException("Failed to save dish - could not retrieve after save");
                });
    }@Transactional(readOnly = true)
    public List<Dish> listDishesByRepId(Integer repId) {
        logger.info("Listing available (amount > 0) dishes for repId: {}", repId);
        List<Dish> availableDishes = dishRepository.findAvailableDishesByRepId(repId);
        logger.info("Found {} available dishes for repId: {}", availableDishes.size(), repId);
        return availableDishes;
    }




    @Transactional
    public Dish updateDishInfo(Long dishId, Integer amount, Double price) {
        logger.info("Updating dish info: dishId={}, amount={}, price={}", dishId, amount, price);

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> {
                    logger.error("Dish not found with ID: {}", dishId);
                    return new RuntimeException("Dish not found");
                });

        if (amount != null) {
            dish.setAmount(amount);
        }
        if (price != null) {
            dish.setPrice(price);
        }

        Dish updatedDish = dishRepository.save(dish);
        logger.info("Successfully updated dish: {}", updatedDish);
        return updatedDish;
    }

    // ✅ Dummy sold dishes with fake shipping info
    @Transactional(readOnly = true)
    public List<String> getDummySoldDishesByRepId(Integer repId) {
        logger.info("Getting dummy sold dishes for repId: {}", repId);
        List<OrderDish> orderDishes = orderDishRepository.findAll();

        List<String> result = new ArrayList<>();

        for (OrderDish od : orderDishes) {
            if ("CONFIRMED".equalsIgnoreCase(od.getOrder().getStatus())
                    && od.getDish().getRepId().equals(repId)) {

                String fakeShipping = getFakeShippingCompany();
                String info = "Dish: " + od.getDish().getDishName() +
                        ", Qty: " + od.getQuantity() +
                        ", Price: " + od.getPriceAtTime() +
                        ", Customer ID: " + od.getOrder().getCustomerId() +
                        ", Shipper: " + fakeShipping;
                result.add(info);
            }
        }

        return result;
    }

    private String getFakeShippingCompany() {
        String[] shippers = {"DHL", "FedEx", "Aramex", "Local Express"};
        return shippers[(int) (Math.random() * shippers.length)];
    }
}
