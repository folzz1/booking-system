package com.example.backend.repository;

import com.example.backend.model.Wing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WingRepository extends JpaRepository<Wing, Long> {
    List<Wing> findAll();

    @Query("SELECT DISTINCT w FROM Wing w " +
            "LEFT JOIN FETCH w.building " +
            "LEFT JOIN FETCH w.rooms")
    List<Wing> findAllWithDetails();
}