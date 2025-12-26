package br.com.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for searching musics with filters")
public record MusicSearchRequest(
        @Schema(description = "Filter by music name (partial match)", example = "Imagine")
        String name,

        @Schema(description = "Filter by genre", example = "Rock")
        String genre,

        @Schema(description = "Filter by author ID", example = "1")
        Long authorId,

        @Schema(description = "Minimum duration in seconds", example = "60")
        Integer minDuration,

        @Schema(description = "Maximum duration in seconds", example = "300")
        Integer maxDuration
) {
}
