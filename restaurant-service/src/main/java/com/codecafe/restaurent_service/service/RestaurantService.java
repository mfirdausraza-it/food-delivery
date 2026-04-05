package com.codecafe.restaurent_service.service;

import com.codecafe.restaurent_service.dto.OrderValidationRequest;
import com.codecafe.restaurent_service.dto.OrderValidationResponse;
import com.codecafe.restaurent_service.dto.ValidatedItemDto;
import com.codecafe.restaurent_service.entity.MenuItem;
import com.codecafe.restaurent_service.entity.Restaurant;
import com.codecafe.restaurent_service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public OrderValidationResponse validateOrderItems(String restaurantId, OrderValidationRequest request) {
        log.info("Validating order items for restaurant {} and customer {}", restaurantId, request.getCustomerId());

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return OrderValidationResponse.builder()
                    .restaurantId(restaurantId)
                    .isValid(false)
                    .message("No items provided for validation")
                    .totalAmount(BigDecimal.ZERO)
                    .validatedItems(List.of())
                    .build();
        }

        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        
        if (restaurantOpt.isEmpty()) {
            return OrderValidationResponse.builder()
                    .restaurantId(restaurantId)
                    .isValid(false)
                    .message("Restaurant not found")
                    .totalAmount(BigDecimal.ZERO)
                    .validatedItems(List.of())
                    .build();
        }

        Restaurant restaurant = restaurantOpt.get();

        if (!"ONLINE".equalsIgnoreCase(restaurant.getStatus())) {
            return OrderValidationResponse.builder()
                    .restaurantId(restaurantId)
                    .isValid(false)
                    .message("Restaurant is currently closed or busy")
                    .totalAmount(BigDecimal.ZERO)
                    .validatedItems(List.of())
                    .build();
        }

        Map<String, MenuItem> menuMap = restaurant.getMenu() != null ?
                restaurant.getMenu().stream().collect(Collectors.toMap(MenuItem::getId, item -> item)) : Map.of();

        List<ValidatedItemDto> validatedItems = request.getItems().stream().map(itemRequest -> {
            String itemId = itemRequest.getItemId();
            Integer quantity = itemRequest.getQuantity();

            MenuItem menuItem = menuMap.get(itemId);
            boolean isAvailable = menuItem != null && menuItem.isAvailable();
            BigDecimal unitPrice = (isAvailable && menuItem != null) ? menuItem.getPrice() : BigDecimal.ZERO;
            String name = menuItem != null ? menuItem.getName() : "Unknown Item";
            BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            return ValidatedItemDto.builder()
                    .itemId(itemId)
                    .name(name)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .subTotal(subTotal)
                    .available(isAvailable)
                    .build();
        }).collect(Collectors.toList());

        boolean allItemsValid = validatedItems.stream().allMatch(ValidatedItemDto::isAvailable);
        
        BigDecimal totalAmount = validatedItems.stream()
                .filter(ValidatedItemDto::isAvailable)
                .map(ValidatedItemDto::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String message = allItemsValid ? "All items validated successfully" : "Some items are not available or do not exist";

        return OrderValidationResponse.builder()
                .restaurantId(restaurantId)
                .isValid(allItemsValid)
                .message(message)
                .totalAmount(totalAmount)
                .validatedItems(validatedItems)
                .build();
    }
}
