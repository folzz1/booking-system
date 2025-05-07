package com.example.backend.repository;

import com.example.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findByRoomIdAndStartTimeBetween(@Param("roomId") Long roomId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.startTime BETWEEN :startTime AND :endTime")
    List<Booking> findByUserIdAndStartTimeBetween(Long userId,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status.name = 'Одобрено' AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findApprovedByRoomIdAndTimeRange(@Param("roomId") Long roomId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
            "((b.startTime BETWEEN :startTime AND :endTime) OR " +
            "(b.endTime BETWEEN :startTime AND :endTime) OR " +
            "(b.startTime <= :startTime AND b.endTime >= :endTime))")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.room r LEFT JOIN FETCH r.building LEFT JOIN FETCH r.wing JOIN FETCH b.status")
    List<Booking> findAllWithDetails();

    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.room r LEFT JOIN FETCH r.building LEFT JOIN FETCH r.wing JOIN FETCH b.status WHERE b.startTime BETWEEN :start AND :end")
    List<Booking> findByStartTimeBetweenWithDetails(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}