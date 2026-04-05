package com.codecafe.restaurent_service.controller;

import com.codecafe.restaurent_service.dto.OrderValidationRequest;
import com.codecafe.restaurent_service.dto.OrderValidationResponse;
import com.codecafe.restaurent_service.entity.Restaurant;
import com.codecafe.restaurent_service.repository.RestaurantRepository;
import com.codecafe.restaurent_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;

    @PostMapping("/{restaurantId}/validate-order")
    public ResponseEntity<OrderValidationResponse> validateOrder(
            @PathVariable String restaurantId,
            @RequestBody OrderValidationRequest request) {
        OrderValidationResponse response = restaurantService.validateOrderItems(restaurantId, request);
        
        if (!response.isValid()) {
            // Depending on requirements, we can return 400 Bad Request or 200 OK with isValid = false
            // We'll use 200 OK because the validation *succeeded* in running, it just found errors.
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurant.getStatus() == null) {
            restaurant.setStatus("ONLINE");
        }
        return ResponseEntity.ok(restaurantRepository.save(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String id) {
        return restaurantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/items/{itemId}/stock")
    public ResponseEntity<String> updateItemStock(@PathVariable String id, @PathVariable String itemId, @RequestParam boolean available) {
        return restaurantRepository.findById(id).map(restaurant -> {
            boolean found = false;
            if (restaurant.getMenu() != null) {
                for (var item : restaurant.getMenu()) {
                    if (itemId.equals(item.getId())) {
                        item.setAvailable(available);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return ResponseEntity.status(404).body("Item not found");
            }
            restaurantRepository.save(restaurant);
            return ResponseEntity.ok("Item stock updated successfully");
        }).orElse(ResponseEntity.status(404).body("Restaurant not found"));
    }
}
