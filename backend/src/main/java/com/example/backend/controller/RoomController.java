package com.example.backend.controller;

import com.example.backend.dto.BuildingDto;
import com.example.backend.dto.RoomDto;
import com.example.backend.dto.WingDto;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import com.example.backend.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final WingRepository wingRepository;

    public RoomController(RoomService roomService,
                          RoomRepository roomRepository,
                          BuildingRepository buildingRepository,
                          WingRepository wingRepository) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
        this.wingRepository = wingRepository;
    }

    @PostMapping
    public ResponseEntity<?> addRoom(@RequestParam String name, @RequestParam Long typeId) {
        try {
            Room room = roomService.createRoom(name, typeId);
            return ResponseEntity.ok(convertToDto(room));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/buildings")
    public List<BuildingDto> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(BuildingDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/wings")
    public List<WingDto> getAllWings() {
        return wingRepository.findAll().stream()
                .map(WingDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredRooms(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long wingId,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer floor) {

        try {
            List<Room> filteredRooms = roomRepository.findFilteredRooms(buildingId, wingId, minCapacity, floor);

            List<RoomDto> result = filteredRooms.stream()
                    .map(room -> new RoomDto(
                            room.getId(),
                            room.getName(),
                            room.getType().getName(),
                            new BuildingDto(room.getBuilding().getId(), room.getBuilding().getName()),
                            room.getWing() != null ?
                                    new WingDto(room.getWing().getId(), room.getWing().getName()) : null,
                            room.getFloor(),
                            room.getCapacity(),
                            room.getArea()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error filtering rooms: " + e.getMessage());
        }
    }

    private RoomDto convertToDto(Room room) {
        return new RoomDto(
                room.getId(),
                room.getName(),
                room.getType().getName(),
                new BuildingDto(room.getBuilding()),
                room.getWing() != null ? new WingDto(room.getWing()) : null,
                room.getFloor(),
                room.getCapacity(),
                room.getArea()
        );
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getRoomsByIds(@RequestParam List<Long> ids) {
        try {
            List<Room> rooms = roomRepository.findAllById(ids);
            List<RoomDto> roomDtos = rooms.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(roomDtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}