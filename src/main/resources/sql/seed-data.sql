INSERT INTO users (
    employee_code,
    full_name,
    email,
    password_hash,
    role,
    gender,
    birth_date,
    is_active,
    created_at,
    created_by
)
VALUES
    -- Admin
    ('ADMIN001', 'Quản Trị Viên Hệ Thống', 'admin@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'admin', 'other', '1980-01-01', true, CURRENT_TIMESTAMP, NULL),
    -- PMs
    ('PM001', 'Nguyễn Văn Quản', 'pm1@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'pm', 'male', '1985-05-15', true, CURRENT_TIMESTAMP, 1),
    ('PM002', 'Trần Thị Linh', 'pm2@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'pm', 'female', '1987-08-22', true, CURRENT_TIMESTAMP, 1),
    ('PM003', 'Lý Văn Hùng', 'pm3@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'pm', 'male', '1986-03-08', true, CURRENT_TIMESTAMP, 1),
    -- DEVs
    ('DEV001', 'Lê Văn Đức', 'dev1@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'dev', 'male', '1990-03-10', true, CURRENT_TIMESTAMP, 1),
    ('DEV002', 'Phạm Thị Mai', 'dev2@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'dev', 'female', '1992-07-18', true, CURRENT_TIMESTAMP, 1),
    ('DEV003', 'Hồ Văn Tuấn', 'dev3@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'dev', 'male', '1991-11-05', true, CURRENT_TIMESTAMP, 1),
    ('DEV004', 'Vũ Ngọc Anh', 'dev4@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'dev', 'female', '1993-01-25', true, CURRENT_TIMESTAMP, 1),
    -- BAs
    ('BA001', 'Đặng Văn Minh', 'ba1@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'ba', 'male', '1988-09-12', true, CURRENT_TIMESTAMP, 1),
    ('BA002', 'Bùi Thị Lan', 'ba2@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'ba', 'female', '1989-04-20', true, CURRENT_TIMESTAMP, 1),
    ('BA003', 'Hoàng Văn Sơn', 'ba3@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'ba', 'male', '1987-11-08', true, CURRENT_TIMESTAMP, 1),
    ('BA004', 'Nguyễn Thị Thu', 'ba4@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'ba', 'female', '1990-12-30', true, CURRENT_TIMESTAMP, 1),
    -- Testers
    ('TEST001', 'Ngô Văn Thắng', 'test1@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'test', 'male', '1990-06-14', true, CURRENT_TIMESTAMP, 1),
    ('TEST002', 'Trần Minh Tâm', 'test2@vgov.vn', '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 'test', 'male', '1992-02-20', true, CURRENT_TIMESTAMP, 1);


-- =====================================================
-- 2. PROJECTS
-- =====================================================
INSERT INTO projects (
    project_code,
    project_name,
    pm_email,
    start_date,
    end_date,
    project_type,
    status,
    description,
    created_at,
    created_by,
    updated_at,
    updated_by
)
VALUES
(
    'VGOV-DOC',
    'Hệ thống quản lý văn bản điện tử',
    'pm1@vgov.vn',
    '2024-01-15',
    '2024-12-31',
    'Package',
    'InProgress',
    'Dự án xây dựng hệ thống quản lý và lưu trữ văn bản điện tử cho các cơ quan chính phủ.',
    CURRENT_TIMESTAMP,
    (SELECT id FROM users WHERE email = 'admin@vgov.vn'),
    CURRENT_TIMESTAMP,
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- =====================================================
-- 3. PROJECT MEMBERS
-- =====================================================
INSERT INTO project_members (
    project_id,
    user_id,
    workload_percentage,
    joined_date,
    is_active,
    created_at,
    created_by
)
VALUES
    -- PM for VGOV-DOC (auto-added when creating project)
    (1, (SELECT id FROM users WHERE email = 'pm1@vgov.vn'), 0, '2024-01-15', true, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE email = 'admin@vgov.vn')),
    -- Members for VGOV-DOC
    (1, (SELECT id FROM users WHERE email = 'dev1@vgov.vn'), 100, '2024-01-20', true, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE email = 'pm1@vgov.vn')),
    (1, (SELECT id FROM users WHERE email = 'dev2@vgov.vn'), 50, '2024-01-22', true, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE email = 'pm1@vgov.vn')),
    (1, (SELECT id FROM users WHERE email = 'ba1@vgov.vn'), 100, '2024-01-18', true, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE email = 'pm1@vgov.vn')),
    (1, (SELECT id FROM users WHERE email = 'test1@vgov.vn'), 100, '2024-02-01', true, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE email = 'pm1@vgov.vn'));


-- =====================================================
-- 4. NOTIFICATIONS
-- =====================================================
INSERT INTO notifications (
    user_id,
    title,
    message,
    notification_type,
    related_project_id,
    related_user_id,
    is_read,
    created_at
)
VALUES
    -- Notifications for members added to VGOV-DOC
    ((SELECT id FROM users WHERE email = 'pm1@vgov.vn'), 'Bạn đã được thêm vào dự án', 'Bạn đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử" với vai trò Quản lý dự án.', 'PROJECT_ASSIGNMENT', 1, (SELECT id FROM users WHERE email = 'admin@vgov.vn'), false, CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE email = 'dev1@vgov.vn'), 'Bạn đã được thêm vào dự án', 'Bạn đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'PROJECT_ASSIGNMENT', 1, (SELECT id FROM users WHERE email = 'pm1@vgov.vn'), false, CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE email = 'dev2@vgov.vn'), 'Bạn đã được thêm vào dự án', 'Bạn đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'PROJECT_ASSIGNMENT', 1, (SELECT id FROM users WHERE email = 'pm1@vgov.vn'), false, CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE email = 'ba1@vgov.vn'), 'Bạn đã được thêm vào dự án', 'Bạn đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'PROJECT_ASSIGNMENT', 1, (SELECT id FROM users WHERE email = 'pm1@vgov.vn'), false, CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE email = 'test1@vgov.vn'), 'Bạn đã được thêm vào dự án', 'Bạn đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'PROJECT_ASSIGNMENT', 1, (SELECT id FROM users WHERE email = 'pm1@vgov.vn'), false, CURRENT_TIMESTAMP),

    -- Notifications for PM about new members
    ((SELECT id FROM users WHERE email = 'pm1@vgov.vn'), 'Thành viên mới trong dự án', 'Lê Văn Đức đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'NEW_MEMBER', 1, (SELECT id FROM users WHERE email = 'dev1@vgov.vn'), false, CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE email = 'pm1@vgov.vn'), 'Thành viên mới trong dự án', 'Phạm Thị Mai đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'NEW_MEMBER', 1, (SELECT id FROM users WHERE email = 'dev2@vgov.vn'), false, CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE email = 'pm1@vgov.vn'), 'Thành viên mới trong dự án', 'Đặng Văn Minh đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'NEW_MEMBER', 1, (SELECT id FROM users WHERE email = 'ba1@vgov.vn'), false, CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE email = 'pm1@vgov.vn'), 'Thành viên mới trong dự án', 'Ngô Văn Thắng đã được thêm vào dự án "Hệ thống quản lý văn bản điện tử".', 'NEW_MEMBER', 1, (SELECT id FROM users WHERE email = 'test1@vgov.vn'), false, CURRENT_TIMESTAMP);
