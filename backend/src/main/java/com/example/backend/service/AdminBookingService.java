package com.example.backend.service;

import com.example.backend.dto.AdminBookingDto;
import com.example.backend.model.Booking;
import com.example.backend.model.BookingStatus;
import com.example.backend.repository.BookingRepository;
import com.example.backend.repository.BookingStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBookingService {
    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;
    public List<AdminBookingDto> getAllBookings() {
        return bookingRepository.findAllWithDetails().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AdminBookingDto> getBookingsByDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            return bookingRepository.findByStartTimeBetweenWithDetails(startOfDay, endOfDay).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты. Ожидается yyyy-MM-dd");
        }
    }

    private AdminBookingDto convertToDto(Booking booking) {
        AdminBookingDto dto = new AdminBookingDto();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserFullName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName());

        dto.setRoomId(booking.getRoom().getId());
        dto.setRoomName(booking.getRoom().getName());
        dto.setRoomType(booking.getRoom().getType() != null ? booking.getRoom().getType().getName() : null);
        dto.setFloor(booking.getRoom().getFloor());

        if (booking.getRoom().getBuilding() != null) {
            dto.setBuildingId(booking.getRoom().getBuilding().getId());
            dto.setBuildingName(booking.getRoom().getBuilding().getName());
        }

        if (booking.getRoom().getWing() != null) {
            dto.setWingId(booking.getRoom().getWing().getId());
            dto.setWingName(booking.getRoom().getWing().getName());
        }

        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus().getName());

        return dto;
    }

    @Transactional
    public void approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));
        BookingStatus approvedStatus = bookingStatusRepository.findByName(BookingStatus.APPROVED)
                .orElseThrow(() -> new IllegalStateException("Статус 'Одобрено' не найден в базе"));
        booking.setStatus(approvedStatus);
        bookingRepository.save(booking);
    }

    @Transactional
    public void rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));
        BookingStatus rejectedStatus = bookingStatusRepository.findByName(BookingStatus.REJECTED)
                .orElseThrow(() -> new IllegalStateException("Статус 'Отклонено' не найден в базе"));
        booking.setStatus(rejectedStatus);
        bookingRepository.save(booking);
    }
}