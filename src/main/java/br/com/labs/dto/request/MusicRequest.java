package br.com.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating or updating a music")
public record MusicRequest(
        @Schema(description = "Music title", example = "Imagine")
        @NotBlank(message = "Music name is required")
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @Schema(description = "Duration in seconds", example = "180")
        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be positive")
        Integer durationSeconds,

        @Schema(description = "Music genre", example = "Rock")
        @Size(max = 100, message = "Genre must be at most 100 characters")
        String genre,

        @Schema(description = "Author ID", example = "1")
        @NotNull(message = "Author ID is required")
        Long authorId
) {
}
