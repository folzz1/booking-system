package com.example.backend.dto;

import com.example.backend.model.Wing;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WingDto {
    private Long id;
    private String name;

    public WingDto(Wing wing) {
        this.id = wing.getId();
        this.name = wing.getName();
    }
}