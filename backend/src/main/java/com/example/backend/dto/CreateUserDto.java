package com.example.backend.dto;

import lombok.Data;

@Data
public class CreateUserDto {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
}