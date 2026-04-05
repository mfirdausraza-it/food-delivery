package com.codecafe.restaurent_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;
}
