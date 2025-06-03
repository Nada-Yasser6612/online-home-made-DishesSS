package com.example.inventory.service;

import com.example.inventory.dto.OrderRequest.DishQuantity;
import com.example.inventory.dto.DishSalesSummary;
import com.example.inventory.dto.OrderStatusUpdate;
import com.example.inventory.model.Dish;
import com.example.inventory.model.Order;
import com.example.inventory.model.OrderDish;
import com.example.inventory.repository.DishRepository;
import com.example.inventory.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private DishRepository dishRepository;
    @Transactional
    public Order makeOrder(List<DishQuantity> dishQuantities, Integer customerId) {
        try {
            Order order = new Order();
            order.setCustomerId(customerId);
            double totalAmount = 0;
            List<OrderDish> orderDishes = new ArrayList<>();

            for (DishQuantity dq : dishQuantities) {
                Long dishId = dq.getDishId();
                Integer quantity = dq.getQuantity();

                Dish dish = dishRepository.findById(dishId)
                        .orElseThrow(() -> {
                            logger.error("Dish not found with id: {}", dishId);
                            return new RuntimeException("Dish not found with id: " + dishId);
                        });

                if (dish.getAmount() < quantity) {
                    logger.error("Not enough dishes in stock for dish: {}. Available: {}, Requested: {}",
                            dish.getDishName(), dish.getAmount(), quantity);
                    throw new RuntimeException("Not enough dishes in stock for dish: " + dish.getDishName());
                }

                OrderDish orderDish = new OrderDish();
                orderDish.setDish(dish);
                orderDish.setQuantity(quantity);
                orderDish.setPriceAtTime(dish.getPrice());
                orderDish.setOrder(order);
                orderDishes.add(orderDish);

                totalAmount += dish.getPrice() * quantity;

                dish.setAmount(dish.getAmount() - quantity);
                dishRepository.save(dish);
            }

            double shippingAmount = totalAmount * 0.1;
            order.setTotalAmount(totalAmount);
            order.setShippingAmount(shippingAmount);
            order.setStatus("PENDING");

            // ✅ Set dishes all at once
            order.setOrderDishes(new HashSet<>(orderDishes));

            Order savedOrder = orderRepository.save(order);
            logger.info("✅ Order saved: ID {}, Total {}, Dishes {}", savedOrder.getId(), totalAmount, orderDishes.size());
            return savedOrder;

        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw e;
        }
    }



    @Transactional(readOnly = true)
    public List<Order> listPastOrdersByCustomerId(Integer customerId) {
        try {
            return orderRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            logger.error("Error listing orders for customerId {}: {}", customerId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Order> listOrdersByRepresentativeId(Integer repId) {
        try {
            return orderRepository.findByOrderDishesDishRepId(repId);
        } catch (Exception e) {
            logger.error("Error listing orders for representativeId {}: {}", repId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<DishSalesSummary> getDishSalesSummaryByRepresentativeId(Integer repId) {
        try {
            List<Order> orders = orderRepository.findByOrderDishesDishRepId(repId);
            Map<Long, DishSalesSummary> summaryMap = new HashMap<>();

            for (Order order : orders) {
                for (OrderDish orderDish : order.getOrderDishes()) {
                    Dish dish = orderDish.getDish();
                    Long dishId = dish.getId();

                    DishSalesSummary summary = summaryMap.computeIfAbsent(dishId, k -> {
                        DishSalesSummary newSummary = new DishSalesSummary();
                        newSummary.setDishId(dish.getId());
                        newSummary.setDishName(dish.getDishName());
                        newSummary.setTotalQuantity(0);
                        newSummary.setTotalRevenue(0.0);
                        return newSummary;
                    });

                    summary.setTotalQuantity(summary.getTotalQuantity() + orderDish.getQuantity());
                    summary.setTotalRevenue(summary.getTotalRevenue() + 
                        (orderDish.getPriceAtTime() * orderDish.getQuantity()));
                }
            }

            // Calculate average prices
            for (DishSalesSummary summary : summaryMap.values()) {
                if (summary.getTotalQuantity() > 0) {
                    summary.setAveragePrice(summary.getTotalRevenue() / summary.getTotalQuantity());
                }
            }

            return new ArrayList<>(summaryMap.values());
        } catch (Exception e) {
            logger.error("Error getting dish sales summary for representativeId {}: {}", repId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id: {}", orderId);
                    return new RuntimeException("Order not found with id: " + orderId);
                });

            order.setStatus(newStatus);
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage(), e);
            throw e;
        }
    }

} 