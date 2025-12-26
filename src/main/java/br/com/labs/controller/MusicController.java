package br.com.labs.controller;

import br.com.labs.dto.request.MusicRequest;
import br.com.labs.dto.request.MusicSearchRequest;
import br.com.labs.dto.response.ApiResponse;
import br.com.labs.dto.response.MusicResponse;
import br.com.labs.dto.response.PageResponse;
import br.com.labs.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/musics")
@Tag(name = "Musics", description = "API for managing musics")
public class MusicController {

    private static final Logger log = LoggerFactory.getLogger(MusicController.class);

    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @PostMapping
    @Operation(summary = "Create a new music", description = "Creates a new music with the provided information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Music created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<MusicResponse>> create(@RequestBody @Valid MusicRequest request) {
        log.info("REST request to create music: {}", request.name());

        MusicResponse music = musicService.create(request);

        log.info("Music created with ID: {}", music.id());
        return ResponseEntity
                .created(URI.create("/api/v1/musics/" + music.id()))
                .body(ApiResponse.success(music, "Music created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get music by ID", description = "Retrieves a music by its unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Music found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Music not found")
    })
    public ResponseEntity<ApiResponse<MusicResponse>> findById(
            @Parameter(description = "Music ID", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get music by ID: {}", id);

        MusicResponse music = musicService.findById(id);

        return ResponseEntity.ok(ApiResponse.success(music));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update music", description = "Updates an existing music with the provided information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Music updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Music or Author not found")
    })
    public ResponseEntity<ApiResponse<MusicResponse>> update(
            @Parameter(description = "Music ID", required = true)
            @PathVariable Long id,
            @RequestBody @Valid MusicRequest request) {
        log.info("REST request to update music ID: {}", id);

        MusicResponse music = musicService.update(id, request);

        log.info("Music updated successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success(music, "Music updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete music", description = "Deletes a music by its unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Music deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Music not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Music ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete music ID: {}", id);

        musicService.delete(id);

        log.info("Music deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all musics", description = "Retrieves a paginated list of all musics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Musics retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<MusicResponse>>> findAll(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("REST request to list musics - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        PageResponse<MusicResponse> musics = musicService.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(musics));
    }

    @GetMapping("/search")
    @Operation(summary = "Search musics", description = "Search musics with dynamic filters using Criteria API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<MusicResponse>>> search(
            @Parameter(description = "Filter by music name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by genre")
            @RequestParam(required = false) String genre,
            @Parameter(description = "Filter by author ID")
            @RequestParam(required = false) Long authorId,
            @Parameter(description = "Minimum duration in seconds")
            @RequestParam(required = false) Integer minDuration,
            @Parameter(description = "Maximum duration in seconds")
            @RequestParam(required = false) Integer maxDuration,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        log.info("REST request to search musics with filters");

        MusicSearchRequest searchRequest = new MusicSearchRequest(name, genre, authorId, minDuration, maxDuration);
        PageResponse<MusicResponse> musics = musicService.search(searchRequest, pageable);

        return ResponseEntity.ok(ApiResponse.success(musics));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get musics by author", description = "Retrieves all musics by a specific author")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Musics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<PageResponse<MusicResponse>>> findByAuthor(
            @Parameter(description = "Author ID", required = true)
            @PathVariable Long authorId,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("REST request to get musics by author ID: {}", authorId);

        PageResponse<MusicResponse> musics = musicService.findByAuthorId(authorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(musics));
    }
}
