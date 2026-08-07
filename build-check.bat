@echo off
setlocal enabledelayedexpansion

:: Make sure docker is on PATH even when double-clicked
set "DOCKER_PATHS=C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\Docker\Docker\resources"
set "PATH=%DOCKER_PATHS%;%PATH%"

cd /d "%~dp0"

echo ============================================================
echo  Step 1/2 - Verify Docker Hub mirror works (pull redis)
echo ============================================================
docker pull redis:7-alpine
echo   redis pull exit code: %errorlevel%
echo.

echo ============================================================
echo  Step 2/2 - Build the h2 service (shows real build error)
echo ============================================================
docker compose build h2
echo   h2 build exit code: %errorlevel%
echo.

echo ============================================================
echo  How to read the result:
echo    - If "redis pull" FAILED  -> your Docker Hub mirror is
echo      NOT working. Re-check Docker Desktop -> Settings ->
echo      Docker Engine -> registry-mirrors, then Apply & Restart.
echo    - If "h2 build" FAILED    -> check the Maven output above
echo      (dependency download). Mirror/network issue inside build.
echo ============================================================
echo.
pause
