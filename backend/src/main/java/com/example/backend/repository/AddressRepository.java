package com.example.backend.repository;

import com.example.backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}