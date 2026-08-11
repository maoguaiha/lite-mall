# =============================================================================
# mall-lite unified launcher (one script starts every "end" of the mall)
#
# The mall currently has 3 client "ends" + 2 backend services + infra:
#   Client ends:
#     1) Web storefront   mall-lite-frontend (Vue3)  -> browse / order
#     2) WeChat mini-app  mall-mini-program          -> browse / order
#     3) Admin console    mall-admin (backend API)   -> operations / admin
#   Backend services:
#     - mall-portal  (user API,  /api)      shared by web + mini-app
#     - mall-admin   (admin API, /admin-api)
#   Infra: H2 database + Redis
#
# Commands (PowerShell):
#   .\mall-lite.ps1            start (default, docker mode)
#   .\mall-lite.ps1 start      same as above
#   .\mall-lite.ps1 stop       stop everything
#   .\mall-lite.ps1 restart    restart
#   .\mall-lite.ps1 status     show container / process status
#   .\mall-lite.ps1 logs       follow logs
#   .\mall-lite.ps1 build      build images / packages only (no start)
#   .\mall-lite.ps1 mini       open the WeChat mini-app only
#   .\mall-lite.ps1 mini-close close WeChat DevTools
#
# Common params:
#   -Mode docker|host   how backends/frontend run (default docker)
#                        docker = full container stack (h2/redis/admin/portal/frontend)
#                        host   = backend jars on host + vite dev frontend (:8088)
#   -NoMini             do not auto-open the WeChat mini-app on start
#
# Double-click entry points: run.bat (start) / stop.bat (stop)
# =============================================================================

param(
    [Parameter(Position = 0)]
    [ValidateSet("start", "stop", "restart", "status", "logs", "build", "mini", "mini-close")]
    [string]$Command = "start",

    [ValidateSet("docker", "host")]
    [string]$Mode = "docker",

    [switch]$NoMini
)

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
# 无论以何种方式启动（双击 run.bat、计划任务、后台进程），都切换到工程根目录，
# 保证后续 mvn / npm / docker compose 都能在正确目录下执行。
Set-Location $root

# Double-click runs close the window instantly on error; trap + pause avoids that.
trap {
    Write-Host "`n[FATAL] $_" -ForegroundColor Red
    Write-Host "Window will close. Copy the message now if needed." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    break
}

function Step($m) { Write-Host "`n==> $m" -ForegroundColor Cyan }

# Docker Desktop often only adds itself to the *user* PATH; prepend common paths.
$dockerPaths = @(
    "C:\Program Files\Docker\Docker\resources\bin",
    "C:\ProgramData\DockerDesktop\version-bin",
    "C:\Program Files\Docker\Docker\resources"
)
$env:PATH = ($dockerPaths -join ";") + ";" + $env:PATH

# Pick compose implementation (v2 plugin preferred, legacy docker-compose fallback)
$useComposeV2 = $false
if (Get-Command docker -ErrorAction SilentlyContinue) {
    & docker compose version > $null 2>&1
    if ($LASTEXITCODE -eq 0) { $useComposeV2 = $true }
}

function Invoke-Compose($ArgumentList) {
    if ($useComposeV2) { & docker compose @ArgumentList }
    else { & docker-compose @ArgumentList }
}

function Assert-Docker {
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Error "docker.exe not found. Please install and start Docker Desktop."
        exit 1
    }
    & docker info > $null 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Docker daemon is not running. Please start Docker Desktop."
        exit 1
    }
}

function Wait-Healthy($name, $max = 60) {
    $prev = $ErrorActionPreference; $ErrorActionPreference = "SilentlyContinue"
    try {
        for ($i = 0; $i -lt $max; $i++) {
            $st = & docker inspect -f '{{.State.Health.Status}}' $name 2>$null
            if ($LASTEXITCODE -eq 0 -and $st -eq "healthy") {
                Write-Host "  [OK] $name healthy"
                return $true
            }
            Start-Sleep -Seconds 2
        }
        Write-Warning "$name did not become healthy in time (docker compose logs $name)"
        return $false
    }
    finally { $ErrorActionPreference = $prev }
}

function Wait-Port($addr, $port, $name, $max = 90) {
    for ($i = 0; $i -lt $max; $i++) {
        $c = $null
        try {
            $c = New-Object System.Net.Sockets.TcpClient
            $c.Connect($addr, $port)
            Write-Host "  [OK] $name listening on :$port"
            return $true
        }
        catch { Start-Sleep -Seconds 2 }
        finally { if ($c) { $c.Dispose() } }
    }
    Write-Warning "$name not reachable on :$port"
    return $false
}

function Assert-PortsFree($ports) {
    $occupied = @()
    foreach ($p in $ports) {
        $t = $null
        try {
            $t = New-Object System.Net.Sockets.TcpClient
            $t.Connect("127.0.0.1", $p)
            $occupied += $p
        }
        catch { }
        finally { if ($t) { $t.Dispose() } }
    }
    if ($occupied.Count -gt 0) {
        Write-Host "The following ports are already in use: $($occupied -join ', ')" -ForegroundColor Red
        Write-Host "A previous mall-lite run is likely still up. Run 'stop' first, or free the ports." -ForegroundColor Yellow
        exit 1
    }
}

# ---- WeChat mini-app: locate DevTools CLI and open the project ----
# NOTE: We scan the filesystem instead of hardcoding the (Chinese) install path,
# because Windows PowerShell misreads non-ASCII literals in a UTF-8-no-BOM .ps1.
function Find-WeChatCli {
    $roots = @("G:\tool\develop", "C:\Program Files (x86)\Tencent", "C:\Program Files\Tencent")
    foreach ($r in $roots) {
        if (Test-Path $r) {
            $found = Get-ChildItem -Path $r -Filter cli.bat -Recurse -Depth 4 -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($found) { return $found.FullName }
        }
    }
    $p = Get-Command cli.bat -ErrorAction SilentlyContinue
    if ($p) { return $p.Source }
    return $null
}

function Open-MiniProgram {
    $cli = Find-WeChatCli
    if (-not $cli) {
        Write-Host "  [SKIP] WeChat DevTools CLI not found; cannot auto-open mini-app." -ForegroundColor Yellow
        Write-Host "          Import manually in DevTools: $root\mall-mini-program" -ForegroundColor Yellow
        return
    }
    # The DevTools CLI is a client of the running IDE; launch the IDE first if needed.
    $ideDir = Split-Path $cli -Parent
    $ideExe = Join-Path $ideDir "wechatdevtools.exe"
    $proc = Get-Process -Name "wechatdevtools" -ErrorAction SilentlyContinue
    if (-not $proc) {
        if (Test-Path $ideExe) {
            Write-Host "  Launching WeChat DevTools IDE..."
            Start-Process -FilePath $ideExe -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 6
        }
        else {
            Write-Host "  [NOTE] IDE exe not found at $ideExe; open DevTools manually, then run 'mini'." -ForegroundColor Yellow
        }
    }
    $proj = Join-Path $root "mall-mini-program"
    Write-Host "  Opening WeChat mini-app: $proj"
    try {
        # Start-Process never raises on native stderr/exit code, so the script stays safe.
        Start-Process -FilePath $cli -ArgumentList "open", "--project", "$proj" -NoNewWindow -ErrorAction SilentlyContinue
        Write-Host "  [OK] mini-app opened in WeChat DevTools (appid: wxb23c20ad538a6cea)" -ForegroundColor Green
    }
    catch {
        Write-Host "  [NOTE] Tried to open mini-app; if DevTools did not pop up, import the folder manually." -ForegroundColor Yellow
    }
}

function Close-MiniProgram {
    $cli = Find-WeChatCli
    if (-not $cli) { Write-Host "  [SKIP] WeChat DevTools CLI not found." -ForegroundColor Yellow; return }
    try {
        Start-Process -FilePath $cli -ArgumentList "quit" -NoNewWindow -ErrorAction SilentlyContinue
        Write-Host "  [OK] Requested WeChat DevTools to close." -ForegroundColor Green
    }
    catch {
        Write-Host "  [NOTE] Failed to request DevTools close." -ForegroundColor Yellow
    }
}

function Print-Access($mode) {
    Write-Host "`n==================== mall access URLs ====================" -ForegroundColor Green
    if ($mode -eq "docker") {
        Write-Host "  [Web storefront]  http://localhost:18088"
        Write-Host "  [Portal API]      http://localhost:18080   (/api)"
        Write-Host "  [Admin API]       http://localhost:18081   (/admin-api)"
    }
    else {
        Write-Host "  [Web storefront]  http://localhost:8088   (vite dev)"
        Write-Host "  [Portal API]      http://localhost:8080   (/api)"
        Write-Host "  [Admin API]       http://localhost:18081   (/admin-api)"
    }
    Write-Host "  [WeChat mini-app]  opened in DevTools; backend http://localhost:8080/api"
    Write-Host "  [Admin frontend]   not included in this repo; admin is served via /admin-api"
    Write-Host "=========================================================" -ForegroundColor Green
}

# =============================================================================
# Command dispatch
# =============================================================================
switch ($Command) {
    "build" {
        Assert-Docker
        Step "Building all images"
        Invoke-Compose @("build")
    }

    "mini" {
        Step "Opening WeChat mini-app only"
        Open-MiniProgram
    }

    "mini-close" {
        Step "Closing WeChat DevTools"
        Close-MiniProgram
    }

    "start" {
        if ($Mode -eq "docker") {
            Assert-Docker
            $running = & docker ps -q --filter "name=mall-lite-" 2>$null
            if ($running) {
                Write-Host "mall-lite is already running." -ForegroundColor Cyan
                if (-not $NoMini) { Open-MiniProgram }
                Print-Access "docker"
                return
            }
            Assert-PortsFree @(18080, 18081, 18088, 8080)
            Step "Starting mall-lite full stack (h2 / redis / admin / portal / frontend)"
            Invoke-Compose @("up", "-d")
            if ($LASTEXITCODE -ne 0) {
                Write-Host "Start failed. Recent logs:" -ForegroundColor Red
                Invoke-Compose @("logs", "--tail=50")
                Write-Error "Aborting. Usually an image pull/build failure; check network or .env REGISTRY mirror."
                exit 1
            }
            Step "Waiting for middleware (h2 / redis) to become healthy"
            Wait-Healthy "mall-lite-h2"
            Wait-Healthy "mall-lite-redis"
            Step "Waiting for backends / frontend ports"
            Wait-Port "127.0.0.1" 18081 "mall-admin"
            Wait-Port "127.0.0.1" 18080 "mall-portal"
            Wait-Port "127.0.0.1" 18088 "mall-frontend"
        }
        else {
            # host mode: backend jars on host + vite dev frontend
            Assert-Docker
            Step "host mode: starting infra (h2 / redis) containers"
            Invoke-Compose @("up", "-d", "h2", "redis")
            Wait-Healthy "mall-lite-h2"
            Wait-Healthy "mall-lite-redis"

            $maven = Get-Command mvn -ErrorAction SilentlyContinue
            $java = Get-Command java -ErrorAction SilentlyContinue
            $npm = Get-Command npm -ErrorAction SilentlyContinue
            if (-not $maven) { Write-Error "mvn not found. Please install Maven."; exit 1 }
            if (-not $java) { Write-Error "java not found. Please install JDK 17."; exit 1 }
            if (-not $npm) { Write-Error "npm not found. Please install Node.js."; exit 1 }

            Assert-PortsFree @(8080, 18081, 8088)
            $logs = Join-Path $root "logs"; $pids = Join-Path $root ".pids"
            New-Item -ItemType Directory -Force -Path $logs, $pids | Out-Null

            Step "Packaging mall-admin"
            mvn -pl mall-admin -am -DskipTests -q package
            Step "Packaging mall-portal"
            mvn -pl mall-portal -am -DskipTests -q package

            $adminJar = Join-Path $root "mall-admin\target\mall-admin-1.0-SNAPSHOT.jar"
            $portalJar = Join-Path $root "mall-portal\target\mall-portal-1.0-SNAPSHOT.jar"
            if (-not (Test-Path $adminJar)) { Write-Error "missing $adminJar"; exit 1 }
            if (-not (Test-Path $portalJar)) { Write-Error "missing $portalJar"; exit 1 }

            Step "Starting mall-admin (:8081)"
            Start-Process -FilePath "java" -WorkingDirectory (Join-Path $root "mall-admin") `
                -ArgumentList "-jar", $adminJar, "--spring.datasource.url=jdbc:h2:tcp://localhost:9092/./mall-shared;MODE=MySQL", "--spring.datasource.username=sa", "--spring.datasource.password=123456" `
                -RedirectStandardOutput (Join-Path $logs "mall-admin.log") `
                -RedirectStandardError (Join-Path $logs "mall-admin.err") `
                -NoNewWindow -PassThru | ForEach-Object { $_.Id | Out-File (Join-Path $pids "mall-admin.pid") }

            Step "Starting mall-portal (:8080)"
            # --spring.* 是应用参数，必须放在 -jar 之后（Java 启动器会把 -jar 前的参数当作 JVM 选项）
            Start-Process -FilePath "java" -WorkingDirectory (Join-Path $root "mall-portal") `
                -ArgumentList "-jar", $portalJar, "--spring.datasource.url=jdbc:h2:tcp://localhost:9092/./mall-shared;MODE=MySQL", "--spring.datasource.username=sa", "--spring.datasource.password=123456" `
                -RedirectStandardOutput (Join-Path $logs "mall-portal.log") `
                -RedirectStandardError (Join-Path $logs "mall-portal.err") `
                -NoNewWindow -PassThru | ForEach-Object { $_.Id | Out-File (Join-Path $pids "mall-portal.pid") }

            Step "Starting frontend dev server (:8088)"
            # npm 在 Windows 上是 npm.cmd（批处理），不能直接用 Start-Process 启动（会报
            # "不是有效的 Win32 应用程序"）。改用 cmd /c 来调用，由 cmd.exe 解释执行。
            Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "npm --prefix mall-lite-frontend run dev" `
                -RedirectStandardOutput (Join-Path $logs "frontend.log") `
                -RedirectStandardError (Join-Path $logs "frontend.err") `
                -NoNewWindow -PassThru | ForEach-Object { $_.Id | Out-File (Join-Path $pids "frontend.pid") }

            Wait-Port "127.0.0.1" 8081 "mall-admin"
            Wait-Port "127.0.0.1" 8080 "mall-portal"
            Wait-Port "127.0.0.1" 8088 "mall-frontend"
        }

        if (-not $NoMini) { Open-MiniProgram }
        Step "mall-lite is up"
        Print-Access $Mode
    }

    "stop" {
        Step "Stopping mall-lite"
        if (Get-Command docker -ErrorAction SilentlyContinue) {
            Invoke-Compose @("down")
        }
        # Also clean up any leftover host-mode background processes
        $pids = Join-Path $root ".pids"
        foreach ($name in @("mall-admin", "mall-portal", "frontend")) {
            $f = Join-Path $pids "$name.pid"
            if (Test-Path $f) {
                $id = (Get-Content $f | Select-Object -First 1).Trim()
                if ($id -match '^\d+$') {
                    Write-Host "  Stopping $name (PID $id)"
                    taskkill /PID $id /T /F 2>$null | Out-Null
                }
                Remove-Item $f -Force
            }
        }
        Write-Host "All services stopped." -ForegroundColor Green
    }

    "restart" {
        & "$PSScriptRoot\mall-lite.ps1" stop
        & "$PSScriptRoot\mall-lite.ps1" start -Mode $Mode -NoMini:$NoMini
    }

    "status" {
        if (Get-Command docker -ErrorAction SilentlyContinue) {
            Invoke-Compose @("ps")
        }
        $pids = Join-Path $root ".pids"
        if (Test-Path $pids) {
            Write-Host "`n[host-mode background processes]"
            foreach ($name in @("mall-admin", "mall-portal", "frontend")) {
                $f = Join-Path $pids "$name.pid"
                if (Test-Path $f) {
                    $id = (Get-Content $f | Select-Object -First 1).Trim()
                    Write-Host "  $name -> PID $id"
                }
            }
        }
    }

    "logs" {
        Assert-Docker
        Invoke-Compose @("logs", "-f")
    }
}

# Pause on double-click (non-interactive) so output stays visible
if (-not $env:WT_SESSION -and [Environment]::UserInteractive) { Read-Host "`nPress Enter to close" }
