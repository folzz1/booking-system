package com.example.backend.service;

import com.example.backend.dto.AdminUserDto;
import com.example.backend.dto.CreateUserDto;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAllWithRoles().stream()
                .map(user -> {
                    AdminUserDto dto = new AdminUserDto();
                    dto.setId(user.getId());
                    dto.setFullName(user.getFirstName() + " " + user.getLastName());
                    dto.setRole(user.getRoles().stream()
                            .findFirst()
                            .map(Role::getName)
                            .orElse("N/A"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        Role role = roleRepository.findByName(createUserDto.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Роль не найдена"));

        User user = new User();
        user.setUsername(createUserDto.getUsername());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }

    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}