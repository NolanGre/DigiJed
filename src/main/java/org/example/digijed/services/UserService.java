package org.example.digijed.services;

import lombok.RequiredArgsConstructor;
import org.example.digijed.dto.RegisterRequestDTO;
import org.example.digijed.models.AuthProvider;
import org.example.digijed.models.User;
import org.example.digijed.models.RoleType;
import org.example.digijed.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(RegisterRequestDTO dto) {
        log.debug("Creating new user from DTO with username: {}", dto.login());
        User user = new User();
        user.setUsername(dto.login());
        user.setPassword(dto.password());
        return createUser(user);
    }

    public User createUser(User user) {
        log.debug("Checking if user exists with username: {}", user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("User creation failed - username already exists: {}", user.getUsername());
            throw new SecurityException("User with this login already exists.");
        }

        log.debug("Setting up new user with username: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(RoleType.USER);
        user.setAuthProvider(AuthProvider.LOCAL);

        log.info("Saving new user to database: {}", user.getUsername());
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        log.debug("Attempting to find user by username: {}", username);
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

}
