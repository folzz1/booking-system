package com.example.backend.service;

import com.example.backend.model.RoomType;
import com.example.backend.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    public RoomType createRoomType(String name) {
        RoomType roomType = new RoomType();
        roomType.setName(name);
        return roomTypeRepository.save(roomType);
    }

    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }
}