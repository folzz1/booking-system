package com.example.backend.service;

import com.example.backend.dto.AdminBuildingDto;
import com.example.backend.model.Building;
import com.example.backend.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBuildingService {
    private final BuildingRepository buildingRepository;

    public List<AdminBuildingDto> getAllBuildings() {
        try {
            // Используем упрощенный запрос без загрузки коллекций
            List<Building> buildings = buildingRepository.findAllWithAddress();

            return buildings.stream()
                    .map(building -> {
                        AdminBuildingDto dto = new AdminBuildingDto();
                        dto.setId(building.getId());
                        dto.setName(building.getName());

                        if (building.getAddress() != null) {
                            dto.setCity(building.getAddress().getCity());
                            dto.setStreet(building.getAddress().getStreet());
                            dto.setBuildingNumber(building.getAddress().getBuildingNumber());
                        }

                        // Заменяем размеры коллекций на вызовы методов репозитория
                        dto.setWingsCount((int) buildingRepository.countWingsByBuildingId(building.getId()));
                        dto.setRoomsCount((int) buildingRepository.countRoomsByBuildingId(building.getId()));

                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка зданий", e);
        }
    }
}