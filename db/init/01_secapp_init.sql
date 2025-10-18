-- ============================================================
-- SecApp - Init Database (MySQL 8.0+)
-- Crea DB, utente applicativo e tutte le tabelle necessarie.
-- Verrà eseguito automaticamente da docker-entrypoint-initdb.d
-- ============================================================

-- 1) Database
CREATE DATABASE IF NOT EXISTS secapp
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 2) Utente applicativo (MySQL 8.0+)
-- Se riavvii spesso, questa forma evita errori se l'utente già esiste
CREATE USER IF NOT EXISTS 'secapp_user'@'%' IDENTIFIED BY 'secapp_pass';
GRANT ALL PRIVILEGES ON secapp.* TO 'secapp_user'@'%';
FLUSH PRIVILEGES;

-- 3) Tabelle
USE secapp;

-- Utenti registrati
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    pwd_hash VARCHAR(100) NOT NULL,
    profile_img_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

-- Token "ricordami" (selector/validator con hash del validator)
CREATE TABLE IF NOT EXISTS remember_tokens (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               user_id BIGINT NOT NULL,
                                               selector CHAR(16) NOT NULL UNIQUE,
    validator_hash CHAR(60) NOT NULL,
    expires_at DATETIME NOT NULL,
    CONSTRAINT fk_rt_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

-- Proposte: titolo + file .txt su disco (niente BLOB).
-- "description" è lasciato NULL per compatibilità ma non usato.
CREATE TABLE IF NOT EXISTS proposals (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    file_path VARCHAR(500) NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prop_user
    FOREIGN KEY (created_by) REFERENCES users(id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

-- 4) Indici utili
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_prop_created_at ON proposals(created_at);
CREATE INDEX IF NOT EXISTS idx_rt_expires_at ON remember_tokens(expires_at);
