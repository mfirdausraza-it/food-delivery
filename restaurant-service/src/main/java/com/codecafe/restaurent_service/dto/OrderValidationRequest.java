package com.codecafe.restaurent_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderValidationRequest {
    private String customerId;
    private List<OrderItemDto> items;
}
