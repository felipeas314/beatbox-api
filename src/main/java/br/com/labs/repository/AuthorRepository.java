package br.com.labs.repository;

import br.com.labs.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {

    Optional<Author> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.musics WHERE a.id = :id")
    Optional<Author> findByIdWithMusics(Long id);
}
