package com.codecafe.order_service.client;

import com.codecafe.order_service.dto.OrderValidationRequest;
import com.codecafe.order_service.dto.OrderValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceClient {

    private final WebClient.Builder webClientBuilder;

    public OrderValidationResponse validateOrder(String restaurantId, OrderValidationRequest request) {
        log.info("Calling restaurant-service to validate order for restaurant ID: {}", restaurantId);
        if (restaurantId == null || restaurantId.isEmpty()) {
            throw new IllegalArgumentException("Restaurant ID cannot be null or empty");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        // This makes a synchronous call using block() since the requirement asks for
        // sync call
        return webClientBuilder.build().post()
                .uri("http://restaurant-service/api/v1/restaurants/{restaurantId}/validate-order", restaurantId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderValidationResponse.class)
                .block();
    }
}
