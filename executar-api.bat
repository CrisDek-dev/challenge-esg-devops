@echo off
chcp 65001 >nul
title Energy Efficiency API
cd /d "%~dp0EnergyEfficiency.Api"

set "DOTNET_EXE=dotnet"
where dotnet >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\dotnet\dotnet.exe" (
        set "DOTNET_EXE=C:\Program Files\dotnet\dotnet.exe"
    ) else (
        echo .NET 8 SDK nao encontrado.
        echo Instale em: https://dotnet.microsoft.com/download/dotnet/8.0
        pause
        exit /b 1
    )
)

echo Restaurando dependencias...
"%DOTNET_EXE%" restore >nul
if errorlevel 1 ( echo Falha no restore. & pause & exit /b 1 )

echo Compilando...
"%DOTNET_EXE%" build --no-restore -v q >nul
if errorlevel 1 ( echo Falha no build. & pause & exit /b 1 )

echo.
echo Iniciando API em http://localhost:5000
echo Swagger sera aberto no navegador em alguns segundos.
echo Para parar a API: feche esta janela ou pressione Ctrl+C.
echo.

start "" cmd /c "timeout /t 8 /nobreak >nul && start http://localhost:5000"

"%DOTNET_EXE%" run --no-build --launch-profile http
pause
