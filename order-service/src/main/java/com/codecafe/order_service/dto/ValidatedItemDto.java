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
public class ValidatedItemDto {
    private String itemId;
    private String name;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    private boolean available;
}
