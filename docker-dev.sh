#!/bin/bash

# V-GOV Backend Docker Development Script
# This script provides convenient commands for Docker development

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_message() {
    echo -e "${GREEN}[V-GOV]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker Desktop and try again."
        exit 1
    fi
}

# Check if docker-compose is available
check_compose() {
    if ! command -v docker-compose >/dev/null 2>&1; then
        print_error "docker-compose is not installed. Please install Docker Compose and try again."
        exit 1
    fi
}

# Function to start the development environment
start_dev() {
    print_message "Starting V-GOV development environment..."
    check_docker
    check_compose
    
    print_info "Building and starting all services..."
    docker-compose up -d --build
    
    print_info "Waiting for services to be healthy..."
    sleep 10
    
    # Check service health
    print_info "Checking service status..."
    docker-compose ps
    
    print_message "Development environment started successfully!"
    print_info "Services available at:"
    echo "  🚀 Backend API: http://localhost:8080"
    echo "  🏥 Health Check: http://localhost:8080/actuator/health"
    echo "  🗄️  Database Admin: http://localhost:8081"
    echo "  🗃️  MinIO Console: http://localhost:9001"
    echo ""
    print_info "Use 'docker-compose logs -f' to view logs"
}

# Function to stop the development environment
stop_dev() {
    print_message "Stopping V-GOV development environment..."
    docker-compose down
    print_message "Development environment stopped."
}

# Function to restart the application
restart_app() {
    print_message "Restarting V-GOV backend application..."
    docker-compose restart vgov-backend
    print_message "Application restarted."
}

# Function to rebuild and restart the application
rebuild_app() {
    print_message "Rebuilding and restarting V-GOV backend application..."
    docker-compose up -d --build vgov-backend
    print_message "Application rebuilt and restarted."
}

# Function to view logs
view_logs() {
    if [ -z "$1" ]; then
        print_info "Showing logs for all services..."
        docker-compose logs -f
    else
        print_info "Showing logs for service: $1"
        docker-compose logs -f "$1"
    fi
}

# Function to access database
access_db() {
    print_message "Accessing PostgreSQL database..."
    docker-compose exec postgres psql -U vgov_user -d vgov
}

# Function to reset database
reset_db() {
    print_warning "This will destroy all data in the database!"
    read -p "Are you sure you want to continue? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_message "Resetting database..."
        docker-compose down postgres
        docker volume rm vgov_postgres_data 2>/dev/null || true
        docker-compose up -d postgres
        print_message "Database reset completed."
    else
        print_info "Database reset cancelled."
    fi
}

# Function to clean up Docker resources
cleanup() {
    print_message "Cleaning up Docker resources..."
    docker-compose down
    docker system prune -f
    print_message "Cleanup completed."
}

# Function to show status
status() {
    print_message "V-GOV Development Environment Status:"
    docker-compose ps
}

# Function to show help
show_help() {
    echo "V-GOV Backend Docker Development Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start       Start the development environment"
    echo "  stop        Stop the development environment"
    echo "  restart     Restart the backend application"
    echo "  rebuild     Rebuild and restart the backend application"
    echo "  logs [svc]  Show logs (optionally for specific service)"
    echo "  db          Access PostgreSQL database shell"
    echo "  reset-db    Reset the database (destroys all data)"
    echo "  status      Show status of all services"
    echo "  cleanup     Clean up Docker resources"
    echo "  help        Show this help message"
    echo ""
    echo "Services:"
    echo "  vgov-backend  - V-GOV Backend Application"
    echo "  postgres      - PostgreSQL Database"
    echo "  minio         - MinIO Object Storage"
    echo "  adminer       - Database Administration Tool"
    echo ""
    echo "Examples:"
    echo "  $0 start                 # Start all services"
    echo "  $0 logs vgov-backend     # Show backend logs"
    echo "  $0 rebuild               # Rebuild and restart backend"
}

# Main script logic
case "$1" in
    start)
        start_dev
        ;;
    stop)
        stop_dev
        ;;
    restart)
        restart_app
        ;;
    rebuild)
        rebuild_app
        ;;
    logs)
        view_logs "$2"
        ;;
    db)
        access_db
        ;;
    reset-db)
        reset_db
        ;;
    status)
        status
        ;;
    cleanup)
        cleanup
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        if [ -z "$1" ]; then
            show_help
        else
            print_error "Unknown command: $1"
            echo ""
            show_help
            exit 1
        fi
        ;;
esac
