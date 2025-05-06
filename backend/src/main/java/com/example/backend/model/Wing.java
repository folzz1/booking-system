package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "wings")
public class Wing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @OneToMany(mappedBy = "wing")
    private Set<Room> rooms;
}