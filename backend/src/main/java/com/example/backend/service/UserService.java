package com.example.backend.service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String firstName, String lastName, String password) {
        return createUser(username, firstName, lastName, password, "USER");
    }

    public User createUser(String username, String firstName, String lastName, String password, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}