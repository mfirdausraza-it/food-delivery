package com.codecafe.order_service.controller;

import com.codecafe.order_service.entity.Order;
import com.codecafe.order_service.repository.OrderRepository;
import com.codecafe.order_service.dto.OrderRequest;
import com.codecafe.order_service.dto.OrderResponse;
import com.codecafe.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@RefreshScope
public class OrderController {

    @Value("${my.custom.message}")
    private String myCustomMessage;

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.placeOrder(orderRequest);

        if ("FAILED".equals(response.getStatus()) || "REJECTED".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        return orderRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(orderRepository.findByCustomerId(customerId));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        return orderRepository.findById(orderId).map(order -> {
            order.setStatus(status);
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/custom-message")
    public ResponseEntity<String> getCustomMessage() {
        return ResponseEntity.ok(myCustomMessage);
    }

}
