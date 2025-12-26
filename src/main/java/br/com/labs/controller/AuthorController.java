package br.com.labs.controller;

import br.com.labs.dto.request.AuthorRequest;
import br.com.labs.dto.response.ApiResponse;
import br.com.labs.dto.response.AuthorResponse;
import br.com.labs.dto.response.AuthorWithMusicsResponse;
import br.com.labs.dto.response.PageResponse;
import br.com.labs.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/authors")
@Tag(name = "Authors", description = "API for managing authors")
public class AuthorController {

    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    @Operation(summary = "Create a new author", description = "Creates a new author with the provided information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Author created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> create(@RequestBody @Valid AuthorRequest request) {
        log.info("REST request to create author: {}", request.email());

        AuthorResponse author = authorService.create(request);

        log.info("Author created with ID: {}", author.id());
        return ResponseEntity
                .created(URI.create("/api/v1/authors/" + author.id()))
                .body(ApiResponse.success(author, "Author created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieves an author by their unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> findById(
            @Parameter(description = "Author ID", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get author by ID: {}", id);

        AuthorResponse author = authorService.findById(id);

        return ResponseEntity.ok(ApiResponse.success(author));
    }

    @GetMapping("/{id}/musics")
    @Operation(summary = "Get author with musics (Cached)",
            description = "Retrieves an author with all their musics. This endpoint uses Redis cache with 5 min TTL.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author found with musics"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<AuthorWithMusicsResponse>> findByIdWithMusics(
            @Parameter(description = "Author ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get author with musics by ID: {} (cached endpoint)", id);

        AuthorWithMusicsResponse author = authorService.findByIdWithMusics(id);

        return ResponseEntity.ok(ApiResponse.success(author));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update author", description = "Updates an existing author with the provided information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> update(
            @Parameter(description = "Author ID", required = true)
            @PathVariable Long id,
            @RequestBody @Valid AuthorRequest request) {
        log.info("REST request to update author ID: {}", id);

        AuthorResponse author = authorService.update(id, request);

        log.info("Author updated successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success(author, "Author updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete author", description = "Deletes an author by their unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Author ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete author ID: {}", id);

        authorService.delete(id);

        log.info("Author deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all authors", description = "Retrieves a paginated list of all authors")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authors retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> findAll(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("REST request to list authors - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        PageResponse<AuthorResponse> authors = authorService.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(authors));
    }
}
