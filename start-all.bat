@echo off
echo ========================================
echo Starting Authorization Service
echo ========================================
echo.

echo [1/2] Starting Backend Server...
start "Backend Server" cmd /k "mvnw.cmd spring-boot:run"

echo [2/2] Waiting 10 seconds for backend to start...
timeout /t 10 /nobreak

echo Starting Frontend Server...
start "Frontend Server" cmd /k "cd ..\auth-frontend && npm start"

echo.
echo ========================================
echo Both servers are starting!
echo ========================================
echo Backend:  http://localhost:8081
echo Frontend: http://localhost:3000
echo API Docs: http://localhost:8081/swagger-ui.html
echo ========================================
echo.
echo Press any key to exit this window...
pause > nul
