package org.example.digijed.controllers;

import lombok.RequiredArgsConstructor;
import org.example.digijed.dto.LoginRequestDTO;
import org.example.digijed.dto.LoginResponseDTO;
import org.example.digijed.dto.RegisterRequestDTO;
import org.example.digijed.dto.RegisterResponseDTO;
import org.example.digijed.models.User;
import org.example.digijed.security.CustomUserDetails;
import org.example.digijed.services.UserService;
import org.example.digijed.services.security.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/")
    public String index() {
        return "Home";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getUsername();
    }

    @PostMapping("/rest-login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String jwt = authenticationService.login(loginRequestDTO.login(), loginRequestDTO.password());
        User user = userService.getUserByUsername(loginRequestDTO.login());

        return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), jwt, user.getId()));
    }

    @PostMapping("/rest-register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO dto) {
        User user = userService.createUser(dto);
        String jwt = authenticationService.register(user);

        return ResponseEntity.ok(new RegisterResponseDTO(user.getUsername(), jwt, user.getId()));
    }
}
