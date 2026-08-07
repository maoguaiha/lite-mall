@echo off
setlocal enabledelayedexpansion

set "DOCKER_PATHS=C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\Docker\Docker\resources"
set "PATH=%DOCKER_PATHS%;%PATH%"

echo Stopping mall-lite full stack (Docker) ...

where docker >nul 2>&1
if errorlevel 1 (
    echo [WARN] docker.exe not found; the stack may already be stopped.
    pause
    exit /b 1
)

cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0mall-lite.ps1" stop

echo.
echo Press any key to close this window ...
pause >nul
