package com.example.backend.controller;

import com.example.backend.dto.RoomDto;
import com.example.backend.model.*;
import com.example.backend.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/available")
    public List<RoomDto> getAvailableRooms(@RequestParam String start,
                                           @RequestParam String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        return bookingService.findOnlyAvailableRooms(startTime, endTime).stream()
                .map(room -> new RoomDto(
                        room.getId(),
                        room.getName(),
                        room.getType().getName()))
                .toList();
    }


    @PostMapping
    public Booking addBooking(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody Booking booking) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        booking.setUser(user);
        return bookingService.createBooking(
                booking.getRoom().getId(),
                user.getId(),
                booking.getStartTime(),
                booking.getEndTime());
    }

    @GetMapping("/user")
    public List<Booking> getUserBookings(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingService.getBookingsForUserOnDate(user.getId(), localDate);
    }
}