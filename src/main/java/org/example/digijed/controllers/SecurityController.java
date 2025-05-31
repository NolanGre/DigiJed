package org.example.digijed.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.digijed.dto.LoginRequestDTO;
import org.example.digijed.dto.LoginResponseDTO;
import org.example.digijed.dto.RegisterRequestDTO;
import org.example.digijed.dto.RegisterResponseDTO;
import org.example.digijed.models.User;
import org.example.digijed.security.CustomOAuth2User;
import org.example.digijed.security.CustomUserDetails;
import org.example.digijed.services.UserService;
import org.example.digijed.services.security.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/api/profile")
    public String profile(@AuthenticationPrincipal Object principal) {

        // Оскільки потрібно реалізувати і сесії і jwt, а OAuth2 працює через браузер (сесії)
        switch (principal) {
            case CustomUserDetails userDetails -> {
                log.info("Principal is CustomUserDetails: {}", userDetails.getUsername());
                return userDetails.getUsername();
            }
            case CustomOAuth2User oAuth2User -> {
                log.info("Principal is CustomOAuth2User: {}", oAuth2User.getName());
                return oAuth2User.getUser().getUsername();
            }
            default -> throw new SecurityException("User not authenticated");
        }
    }

    @PostMapping("/api/rest-login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("Login attempt for user: {}", loginRequestDTO.login());
        String jwt = authenticationService.login(loginRequestDTO.login(), loginRequestDTO.password());
        User user = userService.getUserByUsername(loginRequestDTO.login());

        log.info("User logged in successfully: {}", loginRequestDTO.login());
        return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), jwt, user.getId()));
    }

    @PostMapping("/api/rest-register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO dto) {
        log.info("Attempting to register new user with login: {}", dto.login());
        User user = userService.createUser(dto);
        String jwt = authenticationService.register(user);

        log.info("User registered successfully: {}", dto.login());
        return ResponseEntity.ok(new RegisterResponseDTO(user.getUsername(), jwt, user.getId()));
    }
}
