package com.example.backend.repository;

import com.example.backend.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Long> {
    Optional<BookingStatus> findByName(String name);
}