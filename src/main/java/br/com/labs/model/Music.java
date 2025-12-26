package br.com.labs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "music")
public class Music extends BaseEntity {

    @NotBlank(message = "Music name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Size(max = 100, message = "Genre must be at most 100 characters")
    private String genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private Author author;

    public Music() {
    }

    public Music(String name, Integer durationSeconds, String genre, Author author) {
        this.name = name;
        this.durationSeconds = durationSeconds;
        this.genre = genre;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Music{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", durationSeconds=" + durationSeconds +
                ", genre='" + genre + '\'' +
                '}';
    }
}
