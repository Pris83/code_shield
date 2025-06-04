CREATE TABLE IF NOT EXISTS task (
    id BIGINT  AUTO_INCREMENT PRIMARY KEY,
    assignee_id BIGINT,
    created_at TIMESTAMP,
    description VARCHAR(255),
    due_date TIMESTAMP,
    status VARCHAR(50),
    title VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT INTO users (id,username, email, password) VALUES (1,'john_doe', 'john@example.com', 'password123');

INSERT INTO task (
    title,
    description,
    status,
    due_date,
    created_at,
    assignee_id
) VALUES (
    'Sample Task',
    'This is a test task',
    'IN_PROGRESS',
    '2025-05-29T12:00:00',
    '2025-05-28T10:00:00',
    1
);
