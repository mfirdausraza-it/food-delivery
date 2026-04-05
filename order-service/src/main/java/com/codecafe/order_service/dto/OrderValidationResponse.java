package com.codecafe.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderValidationResponse {
    private String restaurantId;
    private boolean isValid;
    private String message;
    private BigDecimal totalAmount;
    private List<ValidatedItemDto> validatedItems;
}
