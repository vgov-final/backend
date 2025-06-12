-- =====================================================
-- 1. ADMIN ACCOUNT
-- =====================================================

INSERT INTO users (
    employee_code, 
    full_name, 
    email, 
    password_hash, 
    role, 
    gender, 
    birth_date, 
    is_active, 
    created_at
) VALUES (
    'ADMIN001', 
    'Quản Trị Viên Hệ Thống', 
    'admin@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'admin', 
    'other', 
    '1980-01-01', 
    true, 
    CURRENT_TIMESTAMP
);

-- =====================================================
-- 2. PROJECT MANAGER ACCOUNTS (3)
-- =====================================================

-- PM 1
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
) VALUES (
    'PM001', 
    'Nguyễn Văn Quản', 
    'pm1@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'pm', 
    'male', 
    '1985-05-15', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- PM 2
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
) VALUES (
    'PM002', 
    'Trần Thị Linh', 
    'pm2@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'pm', 
    'female', 
    '1987-08-22', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- PM 3
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
) VALUES (
    'PM003', 
    'Lý Văn Hùng', 
    'pm3@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'pm', 
    'male', 
    '1986-03-08', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- =====================================================
-- 3. DEVELOPER ACCOUNTS (2)
-- =====================================================

-- DEV 1
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
) VALUES (
    'DEV001', 
    'Lê Văn Đức', 
    'dev1@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'dev', 
    'male', 
    '1990-03-10', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- DEV 2
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
) VALUES (
    'DEV002', 
    'Phạm Thị Mai', 
    'dev2@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'dev', 
    'female', 
    '1992-07-18', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- DEV 3
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
) VALUES (
    'DEV003', 
    'Hồ Văn Tuấn', 
    'dev3@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'dev', 
    'male', 
    '1991-11-05', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- =====================================================
-- 4. BUSINESS ANALYST ACCOUNTS (2)
-- =====================================================

-- BA 1
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
) VALUES (
    'BA001', 
    'Đặng Văn Minh', 
    'ba1@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'ba', 
    'male', 
    '1988-09-12', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- BA 2
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
) VALUES (
    'BA002', 
    'Bùi Thị Lan', 
    'ba2@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'ba', 
    'female', 
    '1989-04-20', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- BA 3
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
) VALUES (
    'BA003', 
    'Hoàng Văn Sơn', 
    'ba3@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'ba', 
    'male', 
    '1987-11-08', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);

-- =====================================================
-- 5. TEST ACCOUNT (1)
-- =====================================================

-- Tester 1
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
) VALUES (
    'TEST001', 
    'Ngô Văn Thắng', 
    'test1@vgov.vn', 
    '$2y$10$0iM4UYsfcmaLvL3iDXmZpOoR0YEHtYxDVwfQ55irIqwoM0lQNlhrK', 
    'test', 
    'male', 
    '1990-06-14', 
    true, 
    CURRENT_TIMESTAMP, 
    (SELECT id FROM users WHERE email = 'admin@vgov.vn')
);
