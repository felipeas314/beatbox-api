package br.com.labs.dto.response;

import br.com.labs.model.Music;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response DTO for music data")
public record MusicResponse(
        @Schema(description = "Music ID", example = "1")
        Long id,

        @Schema(description = "Music title", example = "Imagine")
        String name,

        @Schema(description = "Duration in seconds", example = "180")
        Integer durationSeconds,

        @Schema(description = "Music genre", example = "Rock")
        String genre,

        @Schema(description = "Author information")
        AuthorSummary author,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
    public static MusicResponse fromEntity(Music music) {
        return new MusicResponse(
                music.getId(),
                music.getName(),
                music.getDurationSeconds(),
                music.getGenre(),
                music.getAuthor() != null ? new AuthorSummary(
                        music.getAuthor().getId(),
                        music.getAuthor().getName()
                ) : null,
                music.getCreatedAt(),
                music.getUpdatedAt()
        );
    }

    @Schema(description = "Summary of author information")
    public record AuthorSummary(
            @Schema(description = "Author ID", example = "1")
            Long id,

            @Schema(description = "Author's name", example = "John Lennon")
            String name
    ) {
    }
}
