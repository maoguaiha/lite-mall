# 兼容入口：统一交由 mall-lite.ps1（host 模式：后端 jar + 前端 vite dev）
# 完整能力见 mall-lite.ps1（start/stop/restart/status/logs/build/mini/mini-close）。
& "$PSScriptRoot\mall-lite.ps1" start -Mode host @args
