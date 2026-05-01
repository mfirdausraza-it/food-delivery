package com.codecafe.restaurent_service.repository;

import com.codecafe.restaurent_service.entity.Restaurant;
import com.codecafe.restaurent_service.entity.MenuItem;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class RestaurantRepository {

    public Optional<Restaurant> findById(String id) {
        Restaurant dummyRestaurant = Restaurant.builder()
                .id(id)
                .name("Dummy Restaurant")
                .address("123 Dummy St")
                .status("ONLINE")
                .menu(List.of(
                        MenuItem.builder().id("item1").name("Burger").price(new BigDecimal("9.99")).available(true).build(),
                        MenuItem.builder().id("item2").name("Fries").price(new BigDecimal("3.99")).available(true).build()
                ))
                .build();
        return Optional.of(dummyRestaurant);
    }

    public Restaurant save(Restaurant restaurant) {
        if (restaurant.getId() == null) {
            restaurant.setId(java.util.UUID.randomUUID().toString());
        }
        return restaurant;
    }

    public List<Restaurant> findAll() {
        return List.of(findById("dummy").get());
    }
}
