package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "booking_statuses")
public class BookingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public static final String PENDING = "В рассмотрении";
    public static final String APPROVED = "Одобрено";
    public static final String REJECTED = "Отклонено";
    public static final String CANCELLED = "Отменено";
    public static final String FREE = "Свободно";
}