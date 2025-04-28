package com.example.backend.service;

import com.example.backend.model.Room;
import com.example.backend.model.RoomType;
import com.example.backend.repository.RoomRepository;
import com.example.backend.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public Room createRoom(String name, Long typeId) {
        RoomType roomType = roomTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Room type not found"));
        Room room = new Room();
        room.setName(name);
        room.setType(roomType);
        return roomRepository.save(room);
    }
}