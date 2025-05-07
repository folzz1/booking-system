package com.example.backend.service;

import com.example.backend.dto.AdminUserDto;
import com.example.backend.model.Role;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {
    private final UserRepository userRepository;

    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAllWithRoles().stream()
                .map(user -> {
                    AdminUserDto dto = new AdminUserDto();
                    dto.setId(user.getId());
                    dto.setFullName(user.getFirstName() + " " + user.getLastName());
                    dto.setEmail(user.getUsername());
                    dto.setRole(user.getRoles().stream()
                            .findFirst()
                            .map(Role::getName)
                            .orElse("N/A"));
                    dto.setActive(user.isEnabled());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}