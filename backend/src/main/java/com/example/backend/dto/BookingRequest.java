package com.example.backend.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long roomId;
    private String startTime;
    private String endTime;
}