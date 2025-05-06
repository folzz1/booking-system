package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String name;
    private String type;
    private BuildingDto building;
    private WingDto wing;
    private Integer floor;
    private Integer capacity;
    private Double area;
}