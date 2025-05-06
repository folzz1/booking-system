package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "buildings")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(mappedBy = "building")
    private Set<Wing> wings;

    @OneToMany(mappedBy = "building")
    private Set<Room> rooms;
}