package com.example.backend.dto;


import lombok.Data;

@Data
public class AdminBuildingDto {
    private Long id;
    private String name;
    private String city;
    private String street;
    private String buildingNumber;
    private Integer wingsCount;
    private Integer roomsCount;
}