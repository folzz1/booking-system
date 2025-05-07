package com.example.backend.repository;

import com.example.backend.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findAll();

    @Query("SELECT DISTINCT b FROM Building b " +
            "LEFT JOIN FETCH b.address " +
            "LEFT JOIN FETCH b.wings " +
            "LEFT JOIN FETCH b.rooms")
    List<Building> findAllWithDetails();

    @Query("SELECT b FROM Building b LEFT JOIN FETCH b.address")
    List<Building> findAllWithAddress();

    @Query("SELECT COUNT(w) FROM Wing w WHERE w.building.id = :buildingId")
    long countWingsByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.building.id = :buildingId")
    long countRoomsByBuildingId(@Param("buildingId") Long buildingId);
}