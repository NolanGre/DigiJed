package org.example.digijed.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record RegisterResponseDTO(
        String username,
        String token,

        @JsonIgnore
        Long id
) {
}
