package com.example.backend.service;

import com.example.backend.dto.AdminWingDto;
import com.example.backend.model.Wing;
import com.example.backend.repository.WingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminWingService {
    private final WingRepository wingRepository;

    public List<AdminWingDto> getAllWings() {
        return wingRepository.findAll().stream()
                .map(wing -> {
                    AdminWingDto dto = new AdminWingDto();
                    dto.setId(wing.getId());
                    dto.setName(wing.getName());
                    dto.setDescription(wing.getDescription());

                    if (wing.getBuilding() != null) {
                        dto.setBuildingId(wing.getBuilding().getId());
                        dto.setBuildingName(wing.getBuilding().getName());
                    }

                    dto.setRoomsCount(0);

                    return dto;
                })
                .collect(Collectors.toList());
    }

}