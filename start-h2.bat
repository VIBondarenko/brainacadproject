@echo off
echo Starting Education Control System - Testing with H2
echo.

REM Set environment variables for H2 testing
set DATABASE_URL=jdbc:h2:mem:testdb
set DATABASE_USERNAME=sa
set DATABASE_PASSWORD=
set ADMIN_USERNAME=admin
set ADMIN_PASSWORD=***REMOVED***
set SERVER_PORT=8080
set LOG_LEVEL_ROOT=INFO
set LOG_LEVEL_APP=DEBUG

echo Environment variables set for H2 testing
echo Database: %DATABASE_URL% (In-Memory H2)
echo Username: %DATABASE_USERNAME%
echo Port: %SERVER_PORT%
echo.

echo Starting application...
mvn spring-boot:run

pause
