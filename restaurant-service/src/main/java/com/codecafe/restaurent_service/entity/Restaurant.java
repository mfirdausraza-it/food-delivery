package com.codecafe.restaurent_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    private String id;
    private String name;
    private String address;
    private String status; // ONLINE, BUSY, OFFLINE
    private List<MenuItem> menu;
}
