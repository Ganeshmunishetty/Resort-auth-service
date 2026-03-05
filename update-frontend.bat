@echo off
echo ========================================
echo Frontend Update Script
echo ========================================
echo.

echo This script will copy the updated frontend files to your auth-frontend folder.
echo.

echo Files to be updated:
echo   1. Register.js
echo   2. AdminDashboard.js
echo   3. api.js
echo.

set /p confirm="Do you want to continue? (Y/N): "
if /i not "%confirm%"=="Y" (
    echo Update cancelled.
    exit /b
)

echo.
echo Creating backups...

if exist "..\auth-frontend\src\components\Register.js" (
    copy "..\auth-frontend\src\components\Register.js" "..\auth-frontend\src\components\Register.js.backup" >nul
    echo   ✓ Backed up Register.js
)

if exist "..\auth-frontend\src\components\AdminDashboard.js" (
    copy "..\auth-frontend\src\components\AdminDashboard.js" "..\auth-frontend\src\components\AdminDashboard.js.backup" >nul
    echo   ✓ Backed up AdminDashboard.js
)

if exist "..\auth-frontend\src\services\api.js" (
    copy "..\auth-frontend\src\services\api.js" "..\auth-frontend\src\services\api.js.backup" >nul
    echo   ✓ Backed up api.js
)

echo.
echo Copying updated files...

copy "frontend-updates\Register.js" "..\auth-frontend\src\components\Register.js" >nul
if %errorlevel% equ 0 (
    echo   ✓ Updated Register.js
) else (
    echo   ✗ Failed to update Register.js
)

copy "frontend-updates\AdminDashboard.js" "..\auth-frontend\src\components\AdminDashboard.js" >nul
if %errorlevel% equ 0 (
    echo   ✓ Updated AdminDashboard.js
) else (
    echo   ✗ Failed to update AdminDashboard.js
)

copy "frontend-updates\api.js" "..\auth-frontend\src\services\api.js" >nul
if %errorlevel% equ 0 (
    echo   ✓ Updated api.js
) else (
    echo   ✗ Failed to update api.js
)

echo.
echo ========================================
echo Update Complete!
echo ========================================
echo.
echo Next steps:
echo   1. Restart your backend server (if running)
echo   2. Restart your frontend server (if running)
echo   3. Test the new features:
echo      - User registration with country code
echo      - Owner registration (pending approval)
echo      - Admin creation from admin dashboard
echo.
echo Backup files saved with .backup extension
echo.
pause
