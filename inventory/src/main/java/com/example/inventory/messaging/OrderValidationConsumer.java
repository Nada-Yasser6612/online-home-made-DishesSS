package com.example.inventory.messaging;

import com.example.inventory.dto.OrderValidationRequest;
import com.example.inventory.dto.OrderValidationResponse;
import com.example.inventory.model.Dish;
import com.example.inventory.model.OrderDish;
import com.example.inventory.repository.DishRepository;
import com.example.inventory.repository.OrderDishRepository;
import com.example.inventory.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Component
public class OrderValidationConsumer {

    private final DishRepository dishRepository;
    private final OrderDishRepository orderDishRepository;
    private final OrderRepository orderRepository;

    private static final Logger LOGGER = Logger.getLogger(OrderValidationConsumer.class.getName());
    private static final double MINIMUM_ORDER_CHARGE = 100.0;

    public OrderValidationConsumer(
            DishRepository dishRepository,
            OrderDishRepository orderDishRepository,
            OrderRepository orderRepository
    ) {
        this.dishRepository = dishRepository;
        this.orderDishRepository = orderDishRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    @RabbitListener(queues = "order.validation.request.queue")
    public OrderValidationResponse handleValidationRequest(@Payload OrderValidationRequest request) {
        Long orderId = Long.valueOf(request.getOrderId());
        LOGGER.info("✅ Received validation request for order ID: " + orderId);

        List<OrderDish> items = orderDishRepository.findWithDishByOrderId(orderId);

        if (items == null || items.isEmpty()) {
            LOGGER.warning("⚠️ Order ID " + orderId + " has no items in DB.");
            orderRepository.updateOrderStatusById(orderId, "REJECTED");
            return new OrderValidationResponse(request.getOrderId(), false, "rejected");
        }

        boolean isValid = true;
        double totalAmount = 0.0;

        for (OrderDish item : items) {
            Dish dish = item.getDish();

            if (dish == null) {
                LOGGER.warning("❌ Dish is null in order item.");
                isValid = false;
                break;
            }

            if (dish.getAmount() < item.getQuantity()) {
                LOGGER.warning("❌ Not enough stock for dish ID: " + dish.getId());
                isValid = false;
                break;
            }

            if (dish.getPrice() == null) {
                LOGGER.warning("❌ Dish price is null for dish ID: " + dish.getId());
                isValid = false;
                break;
            }

            totalAmount += dish.getPrice() * item.getQuantity();
        }

        if (isValid && totalAmount < MINIMUM_ORDER_CHARGE) {
            LOGGER.warning("⚠️ Order total is below minimum charge: " + totalAmount);
            isValid = false;
        }

        String status = isValid ? "CONFIRMED" : "REJECTED";
        String reason = isValid ? "approved" : "rejected";

        orderRepository.updateOrderStatusById(orderId, status);
        LOGGER.info("✅ Validation result for order ID " + orderId + ": " + reason);

        return new OrderValidationResponse(request.getOrderId(), isValid, reason);
    }
}
