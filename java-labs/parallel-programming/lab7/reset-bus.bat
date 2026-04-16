@echo off
setlocal

if not exist C:\tmp (
  echo [INFO] Directory C:\tmp does not exist. Nothing to reset.
  exit /b 0
)

del /q C:\tmp\test-message* 2>nul

if exist C:\tmp\test-message (
  echo [WARN] Bus file still exists: C:\tmp\test-message
  exit /b 1
)

echo [OK] Bus reset complete. Old messages removed.
