@echo off
REM ===================================================
REM   SmartLibrary - Build and Run Script (Windows)
REM   Run this from the SmartLibrary\ folder
REM ===================================================

echo ╔══════════════════════════════════════════╗
echo ║       SmartLibrary - Build and Run       ║
echo ╚══════════════════════════════════════════╝
echo.

REM Create output folder for compiled classes
if not exist "out" mkdir out
if not exist "data" mkdir data

echo [1/2] Compiling Java source files...
javac -d out src/models/*.java src/services/*.java src/ui/*.java src/main/*.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo [2/2] Compilation successful! Starting SmartLibrary...
echo.
java -cp out main.Main
pause
