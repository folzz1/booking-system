package com.example.backend.repository;

import com.example.backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
            "SELECT b.room.id FROM Booking b WHERE " +
            "(b.startTime < :endTime AND b.endTime > :startTime) AND " +
            "(b.status.name = 'Одобрено' OR b.status.name = 'В рассмотрении')" +
            ")")
    List<Room> findAvailableRooms(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Room r " +
            "WHERE (:buildingId IS NULL OR r.building.id = :buildingId) " +
            "AND (:wingId IS NULL OR (r.wing IS NOT NULL AND r.wing.id = :wingId)) " +
            "AND (:minCapacity IS NULL OR r.capacity >= :minCapacity) " +
            "AND (:floor IS NULL OR r.floor = :floor)")
    List<Room> findFilteredRooms(@Param("buildingId") Long buildingId,
                                 @Param("wingId") Long wingId,
                                 @Param("minCapacity") Integer minCapacity,
                                 @Param("floor") Integer floor);
}