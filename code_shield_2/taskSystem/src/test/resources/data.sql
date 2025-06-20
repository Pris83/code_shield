DELETE FROM task;
DELETE FROM users;

-- Users
INSERT INTO users (username, email, password) VALUES ('testuser', 'testuser@example.com', 'password123');

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
    '2025-06-29T12:00:00',
    '2025-06-28T10:00:00',
    (SELECT id FROM users WHERE username = 'testuser')
);
