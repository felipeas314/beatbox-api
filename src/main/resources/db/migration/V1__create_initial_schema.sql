-- V1__create_initial_schema.sql
-- Initial database schema for Music API

CREATE TABLE IF NOT EXISTS author (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS music (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    duration_seconds INTEGER NOT NULL,
    genre VARCHAR(100),
    author_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_music_author FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);

CREATE INDEX idx_author_email ON author(email);
CREATE INDEX idx_music_author_id ON music(author_id);
CREATE INDEX idx_music_genre ON music(genre);
