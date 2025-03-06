CREATE TABLE Todo (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       content VARCHAR(255) NOT NULL,
                       status VARCHAR(255) UNIQUE NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
