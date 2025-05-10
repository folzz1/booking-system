package com.example.backend.service;

import com.example.backend.dto.AdminAddressDto;
import com.example.backend.model.Address;
import com.example.backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAddressService {
    private final AddressRepository addressRepository;

    public List<AdminAddressDto> getAllAddresses() {
        try {
            List<Address> addresses = addressRepository.findAll();

            return addresses.stream()
                    .map(address -> {
                        AdminAddressDto dto = new AdminAddressDto();
                        dto.setId(address.getId());
                        dto.setCity(address.getCity());
                        dto.setStreet(address.getStreet());
                        dto.setBuildingNumber(address.getBuildingNumber());
                        dto.setPostalCode(address.getPostalCode());

                        dto.setBuildingsCount((int) addressRepository.countBuildingsByAddressId(address.getId()));

                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка адресов", e);
        }
    }
}