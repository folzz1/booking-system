package com.example.backend.repository;

import com.example.backend.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findAll();
}