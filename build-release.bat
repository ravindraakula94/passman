@echo off
echo ==========================================
echo     PassMan Release Build Script
echo ==========================================
echo.

echo Checking if keystore configuration is ready...
if not defined KEYSTORE_PASSWORD (
    echo ERROR: KEYSTORE_PASSWORD environment variable not set
    echo Please set your keystore credentials first.
    pause
    exit /b 1
)

echo.
echo Cleaning previous builds...
call gradlew.bat clean

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)

echo.
echo Building release APK...
call gradlew.bat assembleRelease

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Release build failed
    pause
    exit /b 1
)

echo.
echo ==========================================
echo     Build Successful!
echo ==========================================
echo.
echo Release APK location:
echo app\build\outputs\apk\release\app-release.apk
echo.
echo File size:
for %%A in (app\build\outputs\apk\release\app-release.apk) do echo %%~zA bytes
echo.
pause
