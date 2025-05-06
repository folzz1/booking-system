package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BuildingRepository buildingRepository;
    private final WingRepository wingRepository;

    public RoomService(RoomRepository roomRepository,
                       RoomTypeRepository roomTypeRepository,
                       BuildingRepository buildingRepository,
                       WingRepository wingRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.buildingRepository = buildingRepository;
        this.wingRepository = wingRepository;
    }

    public Room createRoom(String name, Long typeId) {
        RoomType roomType = roomTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Room type not found"));
        Room room = new Room();
        room.setName(name);
        room.setType(roomType);
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}