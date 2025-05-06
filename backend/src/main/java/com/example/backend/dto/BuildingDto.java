package com.example.backend.dto;

import com.example.backend.model.Building;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BuildingDto {
    private Long id;
    private String name;

    public BuildingDto(Building building) {
        this.id = building.getId();
        this.name = building.getName();
    }
}