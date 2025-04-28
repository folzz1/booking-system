package com.example.backend.repository;

import com.example.backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.id NOT IN (SELECT b.room.id FROM Booking b WHERE (b.startTime < :endTime AND b.endTime > :startTime))")
    List<Room> findAvailableRooms(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    Optional<Room> findByName(String name);
}