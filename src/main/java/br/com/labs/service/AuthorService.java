package br.com.labs.service;

import br.com.labs.config.RedisConfig;
import br.com.labs.dto.request.AuthorRequest;
import br.com.labs.dto.response.AuthorResponse;
import br.com.labs.dto.response.AuthorWithMusicsResponse;
import br.com.labs.dto.response.PageResponse;
import br.com.labs.exception.BusinessException;
import br.com.labs.exception.ResourceNotFoundException;
import br.com.labs.model.Author;
import br.com.labs.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        log.info("Creating new author with email: {}", request.email());

        if (authorRepository.existsByEmail(request.email())) {
            log.warn("Attempt to create author with existing email: {}", request.email());
            throw new BusinessException("Email already exists: " + request.email());
        }

        Author author = new Author(request.name(), request.email());
        author = authorRepository.save(author);

        log.info("Author created successfully with ID: {}", author.getId());
        return AuthorResponse.fromEntity(author);
    }

    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        log.debug("Fetching author by ID: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Author not found with ID: {}", id);
                    return new ResourceNotFoundException("Author", "id", id);
                });

        log.debug("Author found: {}", author.getName());
        return AuthorResponse.fromEntity(author);
    }

    /**
     * Fetches author with all their musics.
     * This method is cached using Redis to improve performance.
     * Cache key: authorMusics::{authorId}
     * TTL: 5 minutes (configured in RedisConfig)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = RedisConfig.AUTHOR_MUSICS_CACHE, key = "#id")
    public AuthorWithMusicsResponse findByIdWithMusics(Long id) {
        log.info("Cache MISS - Fetching author with musics from database. Author ID: {}", id);

        Author author = authorRepository.findByIdWithMusics(id)
                .orElseThrow(() -> {
                    log.warn("Author not found with ID: {}", id);
                    return new ResourceNotFoundException("Author", "id", id);
                });

        log.info("Author found with {} musics. Caching result for author ID: {}",
                author.getMusics().size(), id);

        return AuthorWithMusicsResponse.fromEntity(author);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.AUTHOR_MUSICS_CACHE, key = "#id")
    public AuthorResponse update(Long id, AuthorRequest request) {
        log.info("Updating author with ID: {} - Cache will be evicted", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Author not found for update with ID: {}", id);
                    return new ResourceNotFoundException("Author", "id", id);
                });

        // Check if email is being changed and if new email already exists
        if (!author.getEmail().equals(request.email()) && authorRepository.existsByEmail(request.email())) {
            log.warn("Attempt to update author with existing email: {}", request.email());
            throw new BusinessException("Email already exists: " + request.email());
        }

        author.setName(request.name());
        author.setEmail(request.email());

        author = authorRepository.save(author);
        log.info("Author updated successfully: {} - Cache evicted", author.getId());

        return AuthorResponse.fromEntity(author);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.AUTHOR_MUSICS_CACHE, key = "#id")
    public void delete(Long id) {
        log.info("Deleting author with ID: {} - Cache will be evicted", id);

        if (!authorRepository.existsById(id)) {
            log.warn("Author not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("Author", "id", id);
        }

        authorRepository.deleteById(id);
        log.info("Author deleted successfully: {} - Cache evicted", id);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuthorResponse> findAll(Pageable pageable) {
        log.debug("Fetching authors page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Author> page = authorRepository.findAll(pageable);

        log.debug("Found {} authors in page", page.getNumberOfElements());
        return PageResponse.fromPage(page, AuthorResponse::fromEntity);
    }
}
