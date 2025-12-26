package br.com.labs.repository.specification;

import br.com.labs.dto.request.MusicSearchRequest;
import br.com.labs.model.Music;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MusicSpecification {

    private MusicSpecification() {
    }

    public static Specification<Music> withFilters(MusicSearchRequest searchRequest) {
        return (Root<Music> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchRequest.name() != null && !searchRequest.name().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + searchRequest.name().toLowerCase() + "%"
                ));
            }

            if (searchRequest.genre() != null && !searchRequest.genre().isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(root.get("genre")),
                        searchRequest.genre().toLowerCase()
                ));
            }

            if (searchRequest.authorId() != null) {
                predicates.add(cb.equal(root.get("author").get("id"), searchRequest.authorId()));
            }

            if (searchRequest.minDuration() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("durationSeconds"), searchRequest.minDuration()));
            }

            if (searchRequest.maxDuration() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("durationSeconds"), searchRequest.maxDuration()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Music> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Music> hasGenre(String genre) {
        return (root, query, cb) -> {
            if (genre == null || genre.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("genre")), genre.toLowerCase());
        };
    }

    public static Specification<Music> hasAuthorId(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("author").get("id"), authorId);
        };
    }

    public static Specification<Music> durationBetween(Integer minDuration, Integer maxDuration) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minDuration != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("durationSeconds"), minDuration));
            }

            if (maxDuration != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("durationSeconds"), maxDuration));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
