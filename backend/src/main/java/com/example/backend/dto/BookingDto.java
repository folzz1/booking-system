package com.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}