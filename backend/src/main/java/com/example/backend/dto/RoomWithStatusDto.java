package com.example.backend.dto;

import com.example.backend.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomWithStatusDto {
    private Long id;
    private String name;
    private String status;
    private String type;

    public boolean isBookable() {
        return status.equals(BookingStatus.FREE);
    }
}