package br.com.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating or updating an author")
public record AuthorRequest(
        @Schema(description = "Author's full name", example = "John Lennon")
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @Schema(description = "Author's email address", example = "john.lennon@beatles.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {
}
