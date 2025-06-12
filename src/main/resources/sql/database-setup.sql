-- V-GOV Database Setup and Business Logic Triggers
-- This script creates the necessary database functions and triggers
-- to enforce business rules as specified in the requirements

-- =====================================================
-- 1. WORKLOAD VALIDATION TRIGGER
-- =====================================================

-- Function to check total workload doesn't exceed 100%
CREATE OR REPLACE FUNCTION check_workload_limit()
RETURNS TRIGGER AS $$
BEGIN
    -- Only check for non-admin users
    IF EXISTS (SELECT 1 FROM users WHERE id = NEW.user_id AND role = 'admin') THEN
        RETURN NEW;
    END IF;
    
    -- Calculate total workload including the new/updated assignment
    IF (SELECT COALESCE(SUM(workload_percentage), 0) 
        FROM project_members 
        WHERE user_id = NEW.user_id 
        AND is_active = true 
        AND (id != NEW.id OR NEW.id IS NULL)) + NEW.workload_percentage > 100 THEN
        RAISE EXCEPTION 'Total workload cannot exceed 100%% for user ID %. Current total would be: %', 
            NEW.user_id,
            (SELECT COALESCE(SUM(workload_percentage), 0) + NEW.workload_percentage
             FROM project_members 
             WHERE user_id = NEW.user_id 
             AND is_active = true 
             AND (id != NEW.id OR NEW.id IS NULL));
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for workload validation
DROP TRIGGER IF EXISTS trigger_check_workload ON project_members;
CREATE TRIGGER trigger_check_workload 
    BEFORE INSERT OR UPDATE ON project_members 
    FOR EACH ROW EXECUTE FUNCTION check_workload_limit();

-- =====================================================
-- 2. ROLE CHANGE VALIDATION TRIGGER
-- =====================================================

-- Function to prevent admin role changes
CREATE OR REPLACE FUNCTION check_role_change()
RETURNS TRIGGER AS $$
BEGIN
    -- Prevent changing TO admin role
    IF NEW.role = 'admin' AND OLD.role != 'admin' THEN
        RAISE EXCEPTION 'Cannot change role to admin. Admin roles can only be created, not promoted.';
    END IF;
    
    -- Prevent changing FROM admin role
    IF OLD.role = 'admin' AND NEW.role != 'admin' THEN
        RAISE EXCEPTION 'Cannot change role from admin. Admin roles cannot be demoted.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for role change validation
DROP TRIGGER IF EXISTS trigger_check_role_change ON users;
CREATE TRIGGER trigger_check_role_change 
    BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION check_role_change();

-- =====================================================
-- 3. WORK LOG VALIDATION TRIGGER
-- =====================================================

-- Function to validate work log entries
CREATE OR REPLACE FUNCTION validate_work_log()
RETURNS TRIGGER AS $$
BEGIN
    -- Check if hours are within valid range (0-24)
    IF NEW.hours_worked < 0 OR NEW.hours_worked > 24 THEN
        RAISE EXCEPTION 'Work hours must be between 0 and 24. Provided: %', NEW.hours_worked;
    END IF;
    
    -- Check if work date is within project timeline
    IF NOT EXISTS (
        SELECT 1 FROM projects p
        JOIN project_members pm ON p.id = pm.project_id
        WHERE p.id = NEW.project_id 
        AND pm.user_id = NEW.user_id
        AND pm.is_active = true
        AND NEW.work_date >= COALESCE(pm.joined_date, p.start_date)
        AND (pm.left_date IS NULL OR NEW.work_date <= pm.left_date)
        AND NEW.work_date >= p.start_date
        AND (p.end_date IS NULL OR NEW.work_date <= p.end_date)
    ) THEN
        RAISE EXCEPTION 'Work date % is outside the valid project timeline or user is not an active member of project %', 
            NEW.work_date, NEW.project_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for work log validation
DROP TRIGGER IF EXISTS trigger_validate_work_log ON work_logs;
CREATE TRIGGER trigger_validate_work_log 
    BEFORE INSERT OR UPDATE ON work_logs 
    FOR EACH ROW EXECUTE FUNCTION validate_work_log();

-- =====================================================
-- 4. PROJECT MEMBER VALIDATION TRIGGER
-- =====================================================

-- Function to validate project member assignments
CREATE OR REPLACE FUNCTION validate_project_member()
RETURNS TRIGGER AS $$
BEGIN
    -- Validate workload percentage is within range
    IF NEW.workload_percentage <= 0 OR NEW.workload_percentage > 100 THEN
        RAISE EXCEPTION 'Workload percentage must be between 0 and 100. Provided: %', NEW.workload_percentage;
    END IF;
    
    -- Validate date range
    IF NEW.left_date IS NOT NULL AND NEW.left_date < NEW.joined_date THEN
        RAISE EXCEPTION 'Left date cannot be before joined date. Joined: %, Left: %', 
            NEW.joined_date, NEW.left_date;
    END IF;
    
    -- Ensure joined date is not before project start date
    IF EXISTS (
        SELECT 1 FROM projects 
        WHERE id = NEW.project_id 
        AND start_date > NEW.joined_date
    ) THEN
        RAISE EXCEPTION 'Employee cannot join project before project start date';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for project member validation
DROP TRIGGER IF EXISTS trigger_validate_project_member ON project_members;
CREATE TRIGGER trigger_validate_project_member 
    BEFORE INSERT OR UPDATE ON project_members 
    FOR EACH ROW EXECUTE FUNCTION validate_project_member();

-- =====================================================
-- 5. PROJECT VALIDATION TRIGGER
-- =====================================================

-- Function to validate project data
CREATE OR REPLACE FUNCTION validate_project()
RETURNS TRIGGER AS $$
BEGIN
    -- Validate end date is after start date
    IF NEW.end_date IS NOT NULL AND NEW.end_date < NEW.start_date THEN
        RAISE EXCEPTION 'Project end date cannot be before start date. Start: %, End: %', 
            NEW.start_date, NEW.end_date;
    END IF;
    
    -- Validate PM email exists and has pm role
    IF NOT EXISTS (
        SELECT 1 FROM users 
        WHERE email = NEW.pm_email 
        AND role = 'pm' 
        AND is_active = true
    ) THEN
        RAISE EXCEPTION 'PM email % must belong to an active user with PM role', NEW.pm_email;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for project validation
DROP TRIGGER IF EXISTS trigger_validate_project ON projects;
CREATE TRIGGER trigger_validate_project 
    BEFORE INSERT OR UPDATE ON projects 
    FOR EACH ROW EXECUTE FUNCTION validate_project();

-- =====================================================
-- 6. NOTIFICATION TRIGGER
-- =====================================================

-- Function to create notifications for project events
CREATE OR REPLACE FUNCTION create_project_notifications()
RETURNS TRIGGER AS $$
BEGIN
    -- Notification for new project member
    IF TG_OP = 'INSERT' AND NEW.is_active = true THEN
        INSERT INTO notifications (user_id, title, message, notification_type, related_project_id, related_user_id)
        SELECT 
            pm.user_id,
            'New Member Added to Project',
            (SELECT u.full_name FROM users u WHERE u.id = NEW.user_id) || ' has been added to project ' || 
            (SELECT p.project_name FROM projects p WHERE p.id = NEW.project_id),
            'PROJECT_MEMBER_ADDED',
            NEW.project_id,
            NEW.user_id
        FROM project_members pm
        WHERE pm.project_id = NEW.project_id 
        AND pm.is_active = true 
        AND pm.user_id != NEW.user_id;
        
        -- Notify the added user
        INSERT INTO notifications (user_id, title, message, notification_type, related_project_id, related_user_id)
        VALUES (
            NEW.user_id,
            'Added to Project',
            'You have been added to project ' || 
            (SELECT p.project_name FROM projects p WHERE p.id = NEW.project_id),
            'PROJECT_MEMBER_ADDED',
            NEW.project_id,
            NEW.user_id
        );
    END IF;
    
    -- Notification for member removal
    IF TG_OP = 'UPDATE' AND OLD.is_active = true AND NEW.is_active = false THEN
        INSERT INTO notifications (user_id, title, message, notification_type, related_project_id, related_user_id)
        SELECT 
            pm.user_id,
            'Member Removed from Project',
            (SELECT u.full_name FROM users u WHERE u.id = NEW.user_id) || ' has been removed from project ' || 
            (SELECT p.project_name FROM projects p WHERE p.id = NEW.project_id),
            'PROJECT_MEMBER_REMOVED',
            NEW.project_id,
            NEW.user_id
        FROM project_members pm
        WHERE pm.project_id = NEW.project_id 
        AND pm.is_active = true 
        AND pm.user_id != NEW.user_id;
    END IF;
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Create trigger for project member notifications
DROP TRIGGER IF EXISTS trigger_project_member_notifications ON project_members;
CREATE TRIGGER trigger_project_member_notifications 
    AFTER INSERT OR UPDATE ON project_members 
    FOR EACH ROW EXECUTE FUNCTION create_project_notifications();

-- Function to create notifications for project status changes
CREATE OR REPLACE FUNCTION create_project_status_notifications()
RETURNS TRIGGER AS $$
BEGIN
    -- Notification for project status change
    IF TG_OP = 'UPDATE' AND OLD.status != NEW.status THEN
        INSERT INTO notifications (user_id, title, message, notification_type, related_project_id)
        SELECT 
            pm.user_id,
            'Project Status Updated',
            'Project ' || NEW.project_name || ' status changed from ' || OLD.status || ' to ' || NEW.status,
            'PROJECT_STATUS_CHANGED',
            NEW.id
        FROM project_members pm
        WHERE pm.project_id = NEW.id 
        AND pm.is_active = true;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for project status notifications
DROP TRIGGER IF EXISTS trigger_project_status_notifications ON projects;
CREATE TRIGGER trigger_project_status_notifications 
    AFTER UPDATE ON projects 
    FOR EACH ROW EXECUTE FUNCTION create_project_status_notifications();

-- =====================================================
-- 7. INDEXES FOR PERFORMANCE
-- =====================================================

-- Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_employee_code ON users(employee_code);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);

CREATE INDEX IF NOT EXISTS idx_projects_code ON projects(project_code);
CREATE INDEX IF NOT EXISTS idx_projects_pm_email ON projects(pm_email);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
CREATE INDEX IF NOT EXISTS idx_projects_type ON projects(project_type);
CREATE INDEX IF NOT EXISTS idx_projects_dates ON projects(start_date, end_date);

CREATE INDEX IF NOT EXISTS idx_project_members_project ON project_members(project_id);
CREATE INDEX IF NOT EXISTS idx_project_members_user ON project_members(user_id);
CREATE INDEX IF NOT EXISTS idx_project_members_active ON project_members(is_active);
CREATE INDEX IF NOT EXISTS idx_project_members_dates ON project_members(joined_date, left_date);

CREATE INDEX IF NOT EXISTS idx_work_logs_user_project ON work_logs(user_id, project_id);
CREATE INDEX IF NOT EXISTS idx_work_logs_date ON work_logs(work_date);
CREATE INDEX IF NOT EXISTS idx_work_logs_project_date ON work_logs(project_id, work_date);

CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_unread ON notifications(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(notification_type);

-- =====================================================
-- 8. VIEWS FOR COMMON QUERIES
-- =====================================================

-- View for user workload summary
CREATE OR REPLACE VIEW user_workload_summary AS
SELECT 
    u.id,
    u.full_name,
    u.email,
    u.role,
    COALESCE(SUM(pm.workload_percentage), 0) as total_workload,
    COUNT(pm.id) as active_projects
FROM users u
LEFT JOIN project_members pm ON u.id = pm.user_id AND pm.is_active = true
WHERE u.is_active = true AND u.role != 'admin'
GROUP BY u.id, u.full_name, u.email, u.role;

-- View for project statistics
CREATE OR REPLACE VIEW project_statistics AS
SELECT 
    p.id,
    p.project_name,
    p.project_code,
    p.status,
    p.project_type,
    p.start_date,
    p.end_date,
    COUNT(pm.id) as member_count,
    COALESCE(SUM(pm.workload_percentage), 0) as total_workload,
    COALESCE(SUM(wl.hours_worked), 0) as total_hours_logged
FROM projects p
LEFT JOIN project_members pm ON p.id = pm.project_id AND pm.is_active = true
LEFT JOIN work_logs wl ON p.id = wl.project_id
GROUP BY p.id, p.project_name, p.project_code, p.status, p.project_type, p.start_date, p.end_date;

-- =====================================================
-- SCRIPT COMPLETION
-- =====================================================

-- Log successful execution
DO $$ 
BEGIN 
    RAISE NOTICE 'V-GOV database setup completed successfully at %', NOW();
    RAISE NOTICE 'All business rule triggers and constraints have been created.';
END $$;
