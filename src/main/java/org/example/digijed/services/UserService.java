package org.example.digijed.services;

import lombok.RequiredArgsConstructor;
import org.example.digijed.dto.RegisterRequestDTO;
import org.example.digijed.models.AuthProvider;
import org.example.digijed.models.User;
import org.example.digijed.models.UserRole;
import org.example.digijed.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(RegisterRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.login());
        user.setPassword(dto.password());
        return createUser(user);
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new SecurityException("User with this login already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER);
        user.setAuthProvider(AuthProvider.LOCAL);

        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: " + userId + " not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

}
