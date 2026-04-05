package com.codecafe.restaurent_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.math.BigDecimal;

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
