@echo off
REM Development Setup Script for Windows

echo ===================================
echo Authorization Service - Dev Setup
echo ===================================

REM Check if .env exists
if not exist .env (
    echo Creating .env file from template...
    copy .env.example .env
    echo [OK] .env file created. Please edit it with your configuration.
) else (
    echo [OK] .env file already exists
)

echo.
echo ===================================
echo Next Steps:
echo ===================================
echo 1. Edit .env file with your database credentials
echo 2. Start MySQL: docker run -d --name auth-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=resort_auth -p 3306:3306 mysql:8.0
echo 3. Run application: mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
echo.
echo Or use Docker Compose:
echo docker-compose up -d
echo.
pause
