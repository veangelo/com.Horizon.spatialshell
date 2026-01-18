@echo off
set DIR=%~dp0
set APP_HOME=%DIR%
set APP_NAME=Gradle

java -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*
