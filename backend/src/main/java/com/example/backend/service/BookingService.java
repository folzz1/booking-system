package com.example.backend.service;

import com.example.backend.dto.RoomWithStatusDto;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;

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

    public List<Booking> getBookingsForUserOnDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return bookingRepository.findByUserIdAndStartTimeBetween(userId, startOfDay, endOfDay);
    }

    public List<Room> findOnlyAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.findAll().stream()
                .filter(room -> {
                    List<Booking> bookings = bookingRepository.findConflictingBookings(
                            room.getId(),
                            startTime,
                            endTime);

                    return bookings.stream().noneMatch(b ->
                            b.getStatus().getName().equals(BookingStatus.APPROVED) ||
                                    b.getStatus().getName().equals(BookingStatus.PENDING));
                })
                .collect(Collectors.toList());
    }

    private boolean isRoomActuallyAvailable(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return true;
        }
        return bookings.stream()
                .noneMatch(b -> b.getStatus().getName().equals(BookingStatus.APPROVED));
    }

    private String determineRoomStatus(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return BookingStatus.FREE;
        }

        // Проверяем есть ли одобренные брони
        boolean hasApproved = bookings.stream()
                .anyMatch(b -> b.getStatus().getName().equals(BookingStatus.APPROVED));

        if (hasApproved) {
            return "Занято";
        }

        // Проверяем есть ли брони в рассмотрении
        boolean hasPending = bookings.stream()
                .anyMatch(b -> b.getStatus().getName().equals(BookingStatus.PENDING));

        return hasPending ? BookingStatus.PENDING : BookingStatus.FREE;
    }
    private boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> approvedBookings = bookingRepository.findApprovedByRoomIdAndTimeRange(
                roomId, startTime, endTime);
        return approvedBookings.isEmpty();
    }
}