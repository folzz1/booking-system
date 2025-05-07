package com.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminBookingDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long roomId;
    private String roomName;
    private String roomType;
    private Long buildingId;
    private String buildingName;
    private Long wingId;
    private String wingName;
    private Integer floor;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}