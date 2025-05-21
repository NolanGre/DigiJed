package org.example.digijed.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginResponseDTO(
        String username,
        String token,

        @JsonIgnore
        Long id
) {
}
