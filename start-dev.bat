@echo off
echo Starting Education Control System - Development Mode
echo.

REM Set environment variables for development
set DATABASE_URL=jdbc:postgresql://localhost:5432/ecs?sslmode=disable
set DATABASE_USERNAME=postgres
set DATABASE_PASSWORD=***REMOVED***
set ADMIN_USERNAME=admin
set ADMIN_PASSWORD=***REMOVED***
set SERVER_PORT=8080
set LOG_LEVEL_ROOT=DEBUG
set LOG_LEVEL_APP=DEBUG

echo Environment variables set for development
echo Database: %DATABASE_URL%
echo Username: %DATABASE_USERNAME%
echo Port: %SERVER_PORT%
echo.

echo Starting application...
mvn spring-boot:run -Dspring.profiles.active=dev

pause
