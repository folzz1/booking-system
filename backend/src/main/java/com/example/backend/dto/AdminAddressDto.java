package com.example.backend.dto;

import lombok.Data;
@Data
public class AdminAddressDto {
    private Long id;
    private String city;
    private String street;
    private String buildingNumber;
    private String postalCode;
    private Integer buildingsCount;
}