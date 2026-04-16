@echo off
setlocal
set "JAVA8_EXE=C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot\bin\java.exe"
if not exist "%JAVA8_EXE%" (
  echo [ERROR] Java 8 not found: "%JAVA8_EXE%"
  exit /b 1
)
if not exist C:\tmp mkdir C:\tmp
"%JAVA8_EXE%" -cp ".;mappedbus.jar" io.mappedbus.sample.object.ObjectReader
