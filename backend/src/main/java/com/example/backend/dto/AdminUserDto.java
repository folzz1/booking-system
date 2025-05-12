package com.example.backend.dto;

import lombok.Data;

@Data
public class AdminUserDto {
    private Long id;
    private String fullName;
    private String role;
}