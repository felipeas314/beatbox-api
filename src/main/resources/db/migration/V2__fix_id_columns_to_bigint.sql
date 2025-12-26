-- V2__fix_id_columns_to_bigint.sql
-- Fix ID columns to use BIGINT (BIGSERIAL) instead of INTEGER (SERIAL)
-- This is needed because the Java entities use Long for IDs

-- First, drop the foreign key constraint
ALTER TABLE music DROP CONSTRAINT IF EXISTS fk_music_author;

-- Change author_id column type in music table
ALTER TABLE music ALTER COLUMN author_id TYPE BIGINT;

-- Change id column type in music table
ALTER TABLE music ALTER COLUMN id TYPE BIGINT;

-- Change id column type in author table
ALTER TABLE author ALTER COLUMN id TYPE BIGINT;

-- Recreate the foreign key constraint
ALTER TABLE music ADD CONSTRAINT fk_music_author
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE;
