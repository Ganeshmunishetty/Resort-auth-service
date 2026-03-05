@echo off
echo ========================================
echo Testing Project Structure
echo ========================================
echo.

echo [1/5] Checking backend folder...
if exist "src\main\java" (
    echo ✓ Backend source code found
) else (
    echo ✗ Backend source code NOT found
)

echo.
echo [2/5] Checking frontend folder...
if exist "..\auth-frontend\src" (
    echo ✓ Frontend source code found
) else (
    echo ✗ Frontend source code NOT found
)

echo.
echo [3/5] Checking frontend dependencies...
if exist "..\auth-frontend\node_modules" (
    echo ✓ Frontend dependencies installed
) else (
    echo ✗ Frontend dependencies NOT installed
    echo   Run: cd ..\auth-frontend ^&^& npm install
)

echo.
echo [4/5] Checking frontend package.json...
if exist "..\auth-frontend\package.json" (
    echo ✓ Frontend package.json found
) else (
    echo ✗ Frontend package.json NOT found
)

echo.
echo [5/5] Checking startup script...
if exist "start-all.bat" (
    echo ✓ Startup script found
) else (
    echo ✗ Startup script NOT found
)

echo.
echo ========================================
echo Structure Test Complete!
echo ========================================
echo.
echo If all checks passed, you can run:
echo   start-all.bat
echo.
pause
