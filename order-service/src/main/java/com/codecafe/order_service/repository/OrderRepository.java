package com.codecafe.order_service.repository;

import com.codecafe.order_service.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class OrderRepository {

    public Order save(Order order) {
        order.setId(UUID.randomUUID().toString());
        return order;
    }

    public java.util.Optional<Order> findById(String id) {
        return java.util.Optional.of(Order.builder().id(id).status("DUMMY_STATUS").build());
    }

    public List<Order> findByCustomerId(String customerId) {
        return new ArrayList<>();
    }
}
