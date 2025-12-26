package br.com.labs.dto.response;

import br.com.labs.model.Author;
import br.com.labs.model.Music;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response DTO for author with their musics")
public record AuthorWithMusicsResponse(
        @Schema(description = "Author ID", example = "1")
        Long id,

        @Schema(description = "Author's full name", example = "John Lennon")
        String name,

        @Schema(description = "Author's email address", example = "john.lennon@beatles.com")
        String email,

        @Schema(description = "List of musics by this author")
        List<MusicSummary> musics,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) implements Serializable {

    public static AuthorWithMusicsResponse fromEntity(Author author) {
        List<MusicSummary> musicSummaries = author.getMusics() != null
                ? author.getMusics().stream()
                    .map(MusicSummary::fromEntity)
                    .toList()
                : List.of();

        return new AuthorWithMusicsResponse(
                author.getId(),
                author.getName(),
                author.getEmail(),
                musicSummaries,
                author.getCreatedAt(),
                author.getUpdatedAt()
        );
    }

    @Schema(description = "Summary of music information")
    public record MusicSummary(
            @Schema(description = "Music ID", example = "1")
            Long id,

            @Schema(description = "Music title", example = "Imagine")
            String name,

            @Schema(description = "Duration in seconds", example = "180")
            Integer durationSeconds,

            @Schema(description = "Music genre", example = "Rock")
            String genre
    ) implements Serializable {

        public static MusicSummary fromEntity(Music music) {
            return new MusicSummary(
                    music.getId(),
                    music.getName(),
                    music.getDurationSeconds(),
                    music.getGenre()
            );
        }
    }
}
