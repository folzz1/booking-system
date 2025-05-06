package com.example.backend.controller;

import com.example.backend.dto.BookingDto;
import com.example.backend.dto.BookingRequest;
import com.example.backend.dto.RoomDto;
import com.example.backend.model.*;
import com.example.backend.repository.BookingStatusRepository;
import com.example.backend.repository.RoomRepository;
import com.example.backend.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final RoomRepository roomRepository;
    private final BookingStatusRepository statusRepository;

    public BookingController(BookingService bookingService,
                             UserService userService,
                             RoomRepository roomRepository,
                             BookingStatusRepository statusRepository) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.roomRepository = roomRepository;
        this.statusRepository = statusRepository;
    }

    @GetMapping("/available")
    public List<RoomDto> getAvailableRooms(@RequestParam String start,
                                           @RequestParam String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);

        return bookingService.findAvailableRooms(startTime, endTime);
    }

    @PostMapping
    public ResponseEntity<?> addBooking(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody BookingRequest bookingRequest) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Booking booking = bookingService.createBooking(
                    bookingRequest.getRoomId(),
                    user.getId(),
                    LocalDateTime.parse(bookingRequest.getStartTime()),
                    LocalDateTime.parse(bookingRequest.getEndTime()));

            BookingDto bookingDto = new BookingDto();
            bookingDto.setId(booking.getId());
            bookingDto.setRoomId(booking.getRoom().getId());
            bookingDto.setUserId(booking.getUser().getId());
            bookingDto.setStartTime(booking.getStartTime());
            bookingDto.setEndTime(booking.getEndTime());
            bookingDto.setStatus(booking.getStatus().getName());

            return ResponseEntity.ok(bookingDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public List<BookingDto> getUserBookings(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingService.getBookingsForUserOnDate(user.getId(), localDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BookingDto convertToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setUserId(booking.getUser().getId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus().getName());
        return dto;
    }

}