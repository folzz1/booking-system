package com.example.backend.repository;

import com.example.backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT DISTINCT a FROM Address a " +
            "LEFT JOIN FETCH a.buildings")
    List<Address> findAllWithDetails();

    @Query("SELECT COUNT(b) FROM Building b WHERE b.address.id = :addressId")
    long countBuildingsByAddressId(@Param("addressId") Long addressId);

    @Query("SELECT a FROM Address a LEFT JOIN FETCH a.buildings")
    List<Address> findAllWithBuildings();
}