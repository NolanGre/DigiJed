package org.example.digijed.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.digijed.models.AuthProvider;
import org.example.digijed.models.User;
import org.example.digijed.repositories.UserRepository;
import org.example.digijed.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String login(String login, String password) {
        log.info("Attempting login for user: {}", login);
        User user = userRepository.findUserByUsername(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            log.warn("Login attempt failed - user {} is not a local account", login);
            throw new SecurityException("Login with password is disabled for this account");
        }

        log.debug("Authenticating user: {}", login);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        String token = jwtService.generateJwtToken(new CustomUserDetails(user));
        log.info("Login successful for user: {}", login);
        return token;
    }

    public String register(User user) {
        log.info("Generating JWT token for newly registered user: {}", user.getUsername());
        String token = jwtService.generateJwtToken(customUserDetailsService.loadUserByUsername(user.getUsername()));
        log.debug("JWT token generated for user: {}", user.getUsername());
        return token;
    }
}
