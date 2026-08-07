@echo off
setlocal enabledelayedexpansion

:: Docker Desktop often adds itself to the *user* PATH only.
:: Double-clicking .bat may not inherit it, so explicitly prepend common locations.
set "DOCKER_PATHS=C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\Docker\Docker\resources"
set "PATH=%DOCKER_PATHS%;%PATH%"

echo Starting mall-lite full stack (Docker) ...
echo.

where docker >nul 2>&1
if errorlevel 1 (
    echo [ERROR] docker.exe not found on PATH.
    echo.
    echo Please check one of the following:
    echo   1. Docker Desktop is installed.
    echo   2. Docker Desktop is running.
    echo   3. Add Docker to PATH manually, e.g.:
    echo      C:\Program Files\Docker\Docker\resources\bin
    echo.
    pause
    exit /b 1
)

docker compose version >nul 2>&1
if errorlevel 1 (
    docker-compose version >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] Neither 'docker compose' nor 'docker-compose' is available.
        echo Please update Docker Desktop or install docker-compose.
        pause
        exit /b 1
    )
)

cd /d "%~dp0"

:: Read the REGISTRY mirror prefix from .env (falls back to empty = official Docker Hub).
set "REGISTRY="
if exist "%~dp0.env" (
    for /f "usebackq tokens=1,* delims==" %%a in (`findstr /b "REGISTRY=" "%~dp0.env"`) do set "REGISTRY=%%b"
)

:: Quick connectivity pre-check: pull redis via the configured mirror before the long build.
echo Pre-check: pulling redis via mirror [%REGISTRY%] to verify network ...
docker pull %REGISTRY%redis:7-alpine
if errorlevel 1 (
    echo.
    echo [ERROR] Could not pull the base image through the configured mirror "%REGISTRY%".
    echo.
    echo The network cannot reach that mirror. Fix options:
    echo   A^) Edit mall-lite\.env and change REGISTRY to another working prefix, e.g.:
    echo        REGISTRY=hub.rat.dev/library/
    echo        REGISTRY=docker.himirror.com/library/
    echo        REGISTRY=docker.m.daocloud.io/library/
    echo      leave a trailing slash after library/
    echo   B^) Or configure Docker Desktop registry-mirrors and set REGISTRY= in .env, then retry.
    echo.
    pause
    exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0mall-lite.ps1" start

echo.
echo Press any key to close this window ...
pause >nul
