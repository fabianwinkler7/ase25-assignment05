package de.unibayreuth.se.taskboard.api.dtos;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record UserDto(

        @Nullable
        UUID id,

        @NotBlank
        String name

) { }
