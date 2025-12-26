package br.com.labs.service;

import br.com.labs.dto.request.MusicRequest;
import br.com.labs.dto.request.MusicSearchRequest;
import br.com.labs.dto.response.MusicResponse;
import br.com.labs.dto.response.PageResponse;
import br.com.labs.exception.BusinessException;
import br.com.labs.exception.ResourceNotFoundException;
import br.com.labs.model.Author;
import br.com.labs.model.Music;
import br.com.labs.repository.AuthorRepository;
import br.com.labs.repository.MusicRepository;
import br.com.labs.repository.specification.MusicSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MusicService {

    private static final Logger log = LoggerFactory.getLogger(MusicService.class);

    private final MusicRepository musicRepository;
    private final AuthorRepository authorRepository;

    public MusicService(MusicRepository musicRepository, AuthorRepository authorRepository) {
        this.musicRepository = musicRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public MusicResponse create(MusicRequest request) {
        log.info("Creating new music: {} for author ID: {}", request.name(), request.authorId());

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    log.warn("Author not found with ID: {}", request.authorId());
                    return new ResourceNotFoundException("Author", "id", request.authorId());
                });

        if (musicRepository.existsByNameAndAuthorId(request.name(), request.authorId())) {
            log.warn("Music already exists with name: {} for author: {}", request.name(), request.authorId());
            throw new BusinessException("Music with this name already exists for this author");
        }

        Music music = new Music(request.name(), request.durationSeconds(), request.genre(), author);
        music = musicRepository.save(music);

        log.info("Music created successfully with ID: {}", music.getId());
        return MusicResponse.fromEntity(music);
    }

    @Transactional(readOnly = true)
    public MusicResponse findById(Long id) {
        log.debug("Fetching music by ID: {}", id);

        Music music = musicRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> {
                    log.warn("Music not found with ID: {}", id);
                    return new ResourceNotFoundException("Music", "id", id);
                });

        log.debug("Music found: {}", music.getName());
        return MusicResponse.fromEntity(music);
    }

    @Transactional
    public MusicResponse update(Long id, MusicRequest request) {
        log.info("Updating music with ID: {}", id);

        Music music = musicRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Music not found for update with ID: {}", id);
                    return new ResourceNotFoundException("Music", "id", id);
                });

        // If author is being changed, verify new author exists
        if (!music.getAuthor().getId().equals(request.authorId())) {
            Author newAuthor = authorRepository.findById(request.authorId())
                    .orElseThrow(() -> {
                        log.warn("New author not found with ID: {}", request.authorId());
                        return new ResourceNotFoundException("Author", "id", request.authorId());
                    });
            music.setAuthor(newAuthor);
        }

        music.setName(request.name());
        music.setDurationSeconds(request.durationSeconds());
        music.setGenre(request.genre());

        music = musicRepository.save(music);
        log.info("Music updated successfully: {}", music.getId());

        return MusicResponse.fromEntity(music);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting music with ID: {}", id);

        if (!musicRepository.existsById(id)) {
            log.warn("Music not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("Music", "id", id);
        }

        musicRepository.deleteById(id);
        log.info("Music deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public PageResponse<MusicResponse> findAll(Pageable pageable) {
        log.debug("Fetching musics page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Music> page = musicRepository.findAll(pageable);

        log.debug("Found {} musics in page", page.getNumberOfElements());
        return PageResponse.fromPage(page, MusicResponse::fromEntity);
    }

    /**
     * Search musics using Criteria API with dynamic filters.
     * This method demonstrates the use of JPA Specifications for flexible querying.
     */
    @Transactional(readOnly = true)
    public PageResponse<MusicResponse> search(MusicSearchRequest searchRequest, Pageable pageable) {
        log.info("Searching musics with filters - name: {}, genre: {}, authorId: {}, duration: {}-{}",
                searchRequest.name(),
                searchRequest.genre(),
                searchRequest.authorId(),
                searchRequest.minDuration(),
                searchRequest.maxDuration());

        Page<Music> page = musicRepository.findAll(
                MusicSpecification.withFilters(searchRequest),
                pageable
        );

        log.info("Search completed. Found {} musics matching criteria", page.getTotalElements());
        return PageResponse.fromPage(page, MusicResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public PageResponse<MusicResponse> findByAuthorId(Long authorId, Pageable pageable) {
        log.debug("Fetching musics for author ID: {}", authorId);

        if (!authorRepository.existsById(authorId)) {
            log.warn("Author not found with ID: {}", authorId);
            throw new ResourceNotFoundException("Author", "id", authorId);
        }

        Page<Music> page = musicRepository.findByAuthorId(authorId, pageable);

        log.debug("Found {} musics for author {}", page.getNumberOfElements(), authorId);
        return PageResponse.fromPage(page, MusicResponse::fromEntity);
    }
}
