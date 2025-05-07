package com.example.backend.dto;

import lombok.Data;

@Data
public class AdminWingDto {
    private Long id;
    private String name;
    private String description;
    private Long buildingId;
    private String buildingName;
    private Integer roomsCount;
}