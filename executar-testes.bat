@echo off
chcp 65001 >nul
title Energy Efficiency - Testes
cd /d "%~dp0"

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

"%DOTNET_EXE%" test EnergyEfficiency.sln --nologo
echo.
pause
