package com.example.backend.controller;

import com.example.backend.model.Room;
import com.example.backend.service.RoomService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public Room addRoom(@RequestParam String name, @RequestParam Long typeId) {
        return roomService.createRoom(name, typeId);
    }
}