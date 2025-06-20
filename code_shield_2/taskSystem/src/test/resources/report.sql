-- Clear existing test data
DELETE FROM task;
DELETE FROM users;

-- Insert test user with explicit ID
INSERT INTO users (id, username, email, password)
VALUES (1, 'testuser', 'test@example.com', 'password');

-- Insert COMPLETED tasks (15 tasks)
INSERT INTO task (
    title,
    description,
    status,
    due_date,
    created_at,
    assignee_id
)
SELECT
    'Completed Task ' || i,
    'Description for Completed Task ' || i,
    'COMPLETED',
    DATE '2025-06-01' + (i % 10),
    DATE '2025-05-25' + (i % 10),
    1
FROM generate_series(1, 15) AS s(i);

-- Insert IN_PROGRESS tasks (5 tasks)
INSERT INTO task (
    title,
    description,
    status,
    due_date,
    created_at,
    assignee_id
)
SELECT
    'In Progress Task ' || i,
    'Description for In Progress Task ' || i,
    'IN_PROGRESS',
    DATE '2025-06-05' + (i % 5),
    DATE '2025-05-30' + (i % 5),
    1
FROM generate_series(1, 5) AS s(i);

-- Insert TODO tasks (2 tasks)
INSERT INTO task (
    title,
    description,
    status,
    due_date,
    created_at,
    assignee_id
)
SELECT
    'To Do Task ' || i,
    'Description for To Do Task ' || i,
    'TODO',
    DATE '2025-06-10' + (i % 2),
    DATE '2025-06-01' + (i % 2),
    1
FROM generate_series(1, 2) AS s(i);

-- Insert CANCELLED task (1 task)
INSERT INTO task (
    title,
    description,
    status,
    due_date,
    created_at,
    assignee_id
)
VALUES (
    'Cancelled Task 1',
    'Description for Cancelled Task 1',
    'CANCELLED',
    DATE '2025-06-12',
    DATE '2025-06-05',
    1
);
