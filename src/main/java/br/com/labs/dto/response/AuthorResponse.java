package br.com.labs.dto.response;

import br.com.labs.model.Author;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response DTO for author data")
public record AuthorResponse(
        @Schema(description = "Author ID", example = "1")
        Long id,

        @Schema(description = "Author's full name", example = "John Lennon")
        String name,

        @Schema(description = "Author's email address", example = "john.lennon@beatles.com")
        String email,

        @Schema(description = "Number of musics by this author", example = "5")
        Integer musicCount,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
    public static AuthorResponse fromEntity(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getMusics() != null ? author.getMusics().size() : 0,
                author.getCreatedAt(),
                author.getUpdatedAt()
        );
    }
}
