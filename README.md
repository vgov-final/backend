# V-GOV Backend - Government Project Management System

## Features
- JWT Authentication & Authorization
- User Management với role-based access control
- Project Management
- Work Log Tracking
- Database schema với PostgreSQL
- Exception Handling
- API Documentation

## Tech Stack
- Java 17
- Spring Boot 3.5.0
- Spring Security với JWT
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## Database Setup
1. Tạo PostgreSQL database:
```sql
CREATE DATABASE vgov;
CREATE USER vgov_user WITH PASSWORD 'vgov_password';
GRANT ALL PRIVILEGES ON DATABASE vgov TO vgov_user;
```

2. Cập nhật connection string trong `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vgov
    username: vgov_user
    password: vgov_password
```

## Running the Application
1. Clone repository
2. Install PostgreSQL và tạo database
3. Run application:
```bash
mvn spring-boot:run
```

## Default Admin Account
- Email: `admin@vgov.vn`
- Password: `admin123`

## API Endpoints

### System APIs (Public)
- `GET /api/system/health` - Health check
- `GET /api/system/version` - Application version

### Authentication APIs
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user info
- `POST /api/auth/refresh` - Refresh token

### User Management APIs (Admin only)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Deactivate user
- `PUT /api/users/{id}/role` - Change user role
- `PUT /api/users/{id}/activate` - Activate/Deactivate user

### Lookup APIs
- `GET /api/lookup/roles` - Get all user roles
- `GET /api/lookup/project-types` - Get all project types
- `GET /api/lookup/project-statuses` - Get all project statuses

## User Roles
- `admin` - Full system access
- `pm` - Project Manager
- `dev` - Developer
- `ba` - Business Analyst
- `test` - Tester

## Sample API Calls

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@vgov.vn",
    "password": "admin123"
  }'
```

### Create User (Admin only)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "employeeCode": "EMP001",
    "fullName": "John Doe",
    "email": "john.doe@vgov.vn",
    "password": "password123",
    "role": "dev",
    "gender": "male",
    "isActive": true
  }'
```

## Environment Variables
```bash
DB_USERNAME=vgov_user
DB_PASSWORD=vgov_password
JWT_SECRET=your-very-long-secret-key-for-jwt-token-signing
SERVER_PORT=8080
LOG_LEVEL=INFO
```

## Development Notes
1. Cấu trúc project theo best practices của Spring Boot
2. Sử dụng DTO pattern cho request/response
3. Role-based security với method-level authorization
4. Global exception handling
5. Audit fields (created_at, updated_at, created_by, updated_by)

## Next Steps
Để hoàn thiện hệ thống, cần implement thêm:
1. Project Management APIs
2. Work Log APIs
3. Notification System
4. File Upload cho profile photos
5. Email notification
6. Reporting APIs
7. Unit Tests
8. Integration Tests
