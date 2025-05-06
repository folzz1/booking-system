package com.example.backend.repository;

import com.example.backend.model.Wing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WingRepository extends JpaRepository<Wing, Long> {
    List<Wing> findAll();
}