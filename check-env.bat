@echo off
setlocal enabledelayedexpansion

:: Prepend common Docker install paths in case they are missing from PATH
set "DOCKER_PATHS=C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\Docker\Docker\resources"
set "PATH=%DOCKER_PATHS%;%PATH%"

set "LOG=%~dp0check-env.log"

(
echo ============================================================
echo                mall-lite environment check
echo ============================================================
echo.

echo [Docker CLI]
where docker 2>nul && (
    docker --version
) || (
    echo   docker.exe: NOT FOUND on PATH
    echo   Searched under: %DOCKER_PATHS%
)
echo.

echo [Docker Compose]
docker compose version 2>nul >nul && (
    docker compose version
    echo   docker compose: OK
) || (
    docker-compose --version 2>nul >nul && (
        docker-compose --version
        echo   docker-compose: OK
    ) || (
        echo   docker compose / docker-compose: NOT AVAILABLE
    )
)
echo.

echo [Docker Daemon]
docker info 2>nul | findstr /I "Server Version" >nul && (
    echo   Docker daemon: RUNNING
) || (
    echo   Docker daemon: NOT RUNNING or not reachable
)
echo.

echo [Java]
where java 2>nul && java -version 2>&1 | findstr "version" || echo   java: NOT FOUND
echo.

echo [Maven]
where mvn 2>nul && mvn -version 2>&1 | findstr "Apache Maven" || echo   mvn: NOT FOUND
echo.

echo [Node.js]
where node 2>nul && node --version || echo   node: NOT FOUND
where npm 2>nul && npm --version || echo   npm: NOT FOUND
echo.

echo [PowerShell]
powershell -NoProfile -Command "Write-Host ('  PowerShell ' + $PSVersionTable.PSVersion)"
echo.

echo ============================================================
echo.
echo If Docker is installed but shows "NOT FOUND", add this folder
echo to your system PATH and restart the terminal / machine:
echo   C:\Program Files\Docker\Docker\resources\bin
echo.
) > "%LOG%" 2>&1

echo Results saved to:
echo   %LOG%
echo.
echo Opening the report now ...
start "" notepad "%LOG%" 2>nul || type "%LOG%"

echo.
echo Press any key to close this window ...
pause >nul
