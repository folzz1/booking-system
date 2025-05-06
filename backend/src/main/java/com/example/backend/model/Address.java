package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String buildingNumber;

    @Column
    private String postalCode;

    @OneToMany(mappedBy = "address")
    private Set<Building> buildings;
}