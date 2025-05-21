package org.example.digijed.services.security;

import lombok.RequiredArgsConstructor;
import org.example.digijed.models.User;
import org.example.digijed.security.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String login(String login, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        } catch (AuthenticationException e) {
            throw new UsernameNotFoundException("User with login: " + login + " not found");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(login);

        return jwtService.generateJwtToken(userDetails);
    }

    public String register(User user) {
        return jwtService.generateJwtToken(customUserDetailsService.loadUserByUsername(user.getUsername()));
    }
}
