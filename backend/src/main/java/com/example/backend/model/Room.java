package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private Double area;

    @Column(nullable = false)
    private Integer capacity;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private RoomType type;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne
    @JoinColumn(name = "wing_id")
    private Wing wing;

    @OneToMany(mappedBy = "room")
    private Set<Booking> bookings;

    @Column(nullable = false)
    private Boolean isActive = true;
}