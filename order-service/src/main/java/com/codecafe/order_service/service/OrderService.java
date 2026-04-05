package com.codecafe.order_service.service;

import com.codecafe.order_service.client.RestaurantServiceClient;
import com.codecafe.order_service.dto.OrderRequest;
import com.codecafe.order_service.dto.OrderResponse;
import com.codecafe.order_service.dto.OrderValidationRequest;
import com.codecafe.order_service.dto.OrderValidationResponse;
import com.codecafe.order_service.entity.Order;
import com.codecafe.order_service.entity.OrderItem;
import com.codecafe.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RestaurantServiceClient restaurantServiceClient;
    private final OrderRepository orderRepository;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        log.info("Processing order for customer: {} at restaurant: {}", orderRequest.getCustomerId(), orderRequest.getRestaurantId());

        OrderValidationRequest validationRequest = OrderValidationRequest.builder()
                .customerId(orderRequest.getCustomerId())
                .items(orderRequest.getItems())
                .build();

        // Synchronous call to Restaurant Service
        OrderValidationResponse validationResponse;
        try {
            validationResponse = restaurantServiceClient.validateOrder(orderRequest.getRestaurantId(), validationRequest);
        } catch (Exception e) {
            log.error("Failed to communicate with restaurant-service", e);
            return OrderResponse.builder()
                    .orderId(null)
                    .status("FAILED")
                    .message("Failed to communicate with Restaurant Service. Please try again later.")
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        if (validationResponse == null || !validationResponse.isValid()) {
            String rejectMsg = (validationResponse != null && validationResponse.getMessage() != null)
                                ? validationResponse.getMessage()
                                : "Order validation failed";
            return OrderResponse.builder()
                    .orderId(null)
                    .status("REJECTED")
                    .message(rejectMsg)
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        // Map validated items from restaurant-service into our Order entity
        List<OrderItem> orderItems = validationResponse.getValidatedItems() != null
                ? validationResponse.getValidatedItems().stream().map(vi -> OrderItem.builder()
                        .itemId(vi.getItemId())
                        .quantity(vi.getQuantity())
                        .unitPrice(vi.getUnitPrice())
                        .totalPrice(vi.getSubTotal())
                        .build())
                  .collect(Collectors.toList())
                : Collections.emptyList();

        // Persist the order in MongoDB
        Order order = Order.builder()
                .customerId(orderRequest.getCustomerId())
                .restaurantId(orderRequest.getRestaurantId())
                .items(orderItems)
                .status("PLACED")
                .message("Order placed successfully")
                .totalAmount(validationResponse.getTotalAmount())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved to MongoDB with ID: {}", savedOrder.getId());

        return OrderResponse.builder()
                .orderId(savedOrder.getId())
                .status("PLACED")
                .message("Order placed successfully")
                .totalAmount(savedOrder.getTotalAmount())
                .build();
    }
}
