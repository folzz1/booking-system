package com.example.backend.controller;

import com.example.backend.dto.AdminAddressDto;
import com.example.backend.dto.AdminBuildingDto;
import com.example.backend.dto.AdminWingDto;
import com.example.backend.dto.CreateUserDto;
import com.example.backend.service.AdminAddressService;
import com.example.backend.service.AdminBookingService;
import com.example.backend.service.AdminBuildingService;
import com.example.backend.service.AdminUserService;
import com.example.backend.service.AdminWingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/api")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminPanelController {

    private final AdminUserService userService;
    private final AdminBuildingService buildingService;
    private final AdminWingService wingService;
    private final AdminAddressService addressService;
    private final AdminBookingService bookingService;

    public AdminPanelController(AdminUserService userService,
                                AdminBuildingService buildingService,
                                AdminWingService wingService,
                                AdminAddressService addressService,
                                AdminBookingService bookingService) {
        this.userService = userService;
        this.buildingService = buildingService;
        this.wingService = wingService;
        this.addressService = addressService;
        this.bookingService = bookingService;
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            return ResponseEntity.ok(userService.getAllRoles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении списка ролей: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении списка пользователей: " + e.getMessage());
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserDto createUserDto) {
        try {
            userService.createUser(createUserDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/buildings")
    public ResponseEntity<?> getAllBuildings() {
        try {
            List<AdminBuildingDto> buildings = buildingService.getAllBuildings();
            return ResponseEntity.ok(buildings);
        } catch (Exception e) {
            String errorMessage = "Ошибка при получении списка зданий: " +
                    (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }

    @GetMapping("/wings")
    public ResponseEntity<?> getAllWings() {
        try {
            System.out.println("Attempting to fetch wings...");
            List<AdminWingDto> wings = wingService.getAllWings();
            System.out.println("Successfully fetched " + wings.size() + " wings");
            return ResponseEntity.ok(wings);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Ошибка при получении списка крыльев: " +
                    (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAllAddresses() {
        try {
            List<AdminAddressDto> addresses = addressService.getAllAddresses();
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            String errorMessage = "Ошибка при получении списка адресов: " +
                    (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings(@RequestParam(required = false) String date) {
        try {
            if (date != null) {
                return ResponseEntity.ok(bookingService.getBookingsByDate(date));
            }
            return ResponseEntity.ok(bookingService.getAllBookings());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении списка бронирований: " + e.getMessage());
        }
    }

    @PostMapping("/bookings/{id}/approve")
    public ResponseEntity<?> approveBooking(@PathVariable Long id) {
        try {
            bookingService.approveBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при одобрении бронирования: " + e.getMessage());
        }
    }

    @PostMapping("/bookings/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id) {
        try {
            bookingService.rejectBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при отклонении бронирования: " + e.getMessage());
        }
    }
}