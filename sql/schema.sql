-- Crea DB/utente/tabelle
CREATE DATABASE IF NOT EXISTS secapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'secapp_user'@'localhost' IDENTIFIED BY 'secapp_pass';
GRANT ALL PRIVILEGES ON secapp.* TO 'secapp_user'@'localhost';
FLUSH PRIVILEGES;

USE secapp;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    pwd_hash VARCHAR(100) NOT NULL,
    profile_img_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
