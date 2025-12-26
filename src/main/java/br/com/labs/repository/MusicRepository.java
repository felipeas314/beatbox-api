package br.com.labs.repository;

import br.com.labs.model.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long>, JpaSpecificationExecutor<Music> {

    List<Music> findByAuthorId(Long authorId);

    Page<Music> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT m FROM Music m JOIN FETCH m.author WHERE m.id = :id")
    Optional<Music> findByIdWithAuthor(Long id);

    List<Music> findByGenreIgnoreCase(String genre);

    boolean existsByNameAndAuthorId(String name, Long authorId);
}
