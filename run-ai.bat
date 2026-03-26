@echo off
setlocal enabledelayedexpansion
cls

echo ============================================================
echo         SPRING AI ENTERPRISE - SELETTORE MODELLI
echo ============================================================
echo.
echo  1) GEMINI    (Google GenAI)
echo  2) CLAUDE    (Anthropic)
echo  3) OLLAMA    (Locale)
echo  4) OPENAI    (GPT-4o / SDK)
echo  5) AZURE     (Microsoft OpenAI)
echo  6) BEDROCK   (AWS Converse)
echo  7) MULTIMEDIA (Stability AI / ElevenLabs)
echo  8) OTHERS    (Mistral / DeepSeek / HF)
echo  9) MOONSHOT  (Isolato)
echo.
echo ============================================================

set /p choice="Seleziona il modello (1-9): "

if "%choice%"=="1" set PROFILE=gemini
if "%choice%"=="2" set PROFILE=claude
if "%choice%"=="3" set PROFILE=ollama
if "%choice%"=="4" set PROFILE=openai
if "%choice%"=="5" set PROFILE=azure
if "%choice%"=="6" set PROFILE=bedrock
if "%choice%"=="7" set PROFILE=multimedia
if "%choice%"=="8" set PROFILE=others
if "%choice%"=="9" set PROFILE=moonshot

if "!PROFILE!"=="" (
    echo Scelta non valida. Uscita.
    pause
    exit /b
)

echo.
echo [INFO] Avvio in corso con il Profilo: !PROFILE!
echo [INFO] Maven carichera' solo le dipendenze dedicate...
echo [INFO] Spring Boot attivera' la configurazione specifica...
echo.

:: Esecuzione del comando Maven
:: -P attiva il profilo nel pom.xml
:: -Dspring-boot.run.profiles attiva il profilo nello yaml
mvn spring-boot:run -P!PROFILE! -Dspring-boot.run.profiles=!PROFILE!

pause


