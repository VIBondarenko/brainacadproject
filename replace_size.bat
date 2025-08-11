@echo off
setlocal enabledelayedexpansion

set "inputFile=src\main\java\com\brainacad\ecs\Storage.java"
set "tempFile=src\main\java\com\brainacad\ecs\Storage_temp.java"

for /f "delims=" %%i in (%inputFile%) do (
    set "line=%%i"
    set "line=!line:.size() == 0=.isEmpty()!"
    echo !line!>>%tempFile%
)

move %tempFile% %inputFile%
echo Replacement complete.
