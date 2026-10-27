@echo off
chcp 65001 >nul
title Energy Efficiency - Testes BDD (Cucumber)
cd /d "%~dp0testes-automatizados"

rem Endereco da API testada. Padrao: API do executar-api.bat (porta 5000).
rem Para testar a API no Docker, antes rode: set API_BASE_URL=http://localhost:8080
if "%API_BASE_URL%"=="" set "API_BASE_URL=http://localhost:5000"

where java >nul 2>nul
if errorlevel 1 (
    echo Java nao encontrado. Instale o JDK 21 ou superior: https://adoptium.net
    pause
    exit /b 1
)

echo Executando os testes BDD contra %API_BASE_URL%
echo (a API precisa estar rodando - veja o README)
echo.

set "MAVEN_OPTS=-Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
call "%~dp0testes-automatizados\mvnw.cmd" -B clean test "-Dapi.baseUrl=%API_BASE_URL%"
set "RESULTADO=%ERRORLEVEL%"

echo.
if exist "target\cucumber-reports.html" (
    echo Relatorio HTML: %cd%\target\cucumber-reports.html
    if not defined SEM_ABRIR_RELATORIO start "" "target\cucumber-reports.html"
)
pause
exit /b %RESULTADO%
