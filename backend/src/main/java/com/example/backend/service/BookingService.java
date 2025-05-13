package com.example.backend.service;

import com.example.backend.dto.BuildingDto;
import com.example.backend.dto.RoomDto;
import com.example.backend.dto.RoomWithStatusDto;
import com.example.backend.dto.WingDto;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingStatusRepository statusRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository,
                          BookingStatusRepository statusRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomDto> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.findAvailableRooms(startTime, endTime).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Booking createBooking(Long roomId, Long userId,
                                 LocalDateTime startTime, LocalDateTime endTime) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BookingStatus pendingStatus = statusRepository.findByName(BookingStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        if (!isRoomAvailable(roomId, startTime, endTime)) {
            throw new RuntimeException("Room is not available for the selected time.");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(pendingStatus);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsForUserOnDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return bookingRepository.findByUserIdAndStartTimeBetween(userId, startOfDay, endOfDay);
    }

    @Transactional(readOnly = true)
    public List<RoomDto> findOnlyAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.findAvailableRooms(startTime, endTime).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RoomDto convertToDto(Room room) {
        return new RoomDto(
                room.getId(),
                room.getName(),
                room.getType().getName(),
                new BuildingDto(room.getBuilding().getId(), room.getBuilding().getName()),
                room.getWing() != null ? new WingDto(room.getWing().getId(), room.getWing().getName()) : null,
                room.getFloor(),
                room.getCapacity(),
                room.getArea()
        );
    }

    private boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findApprovedByRoomIdAndTimeRange(roomId, startTime, endTime).isEmpty();
    }

    public void cancelBooking(Long id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Вы можете отменять только свои бронирования");
        }

        bookingRepository.delete(booking);
    }
}