@echo off
REM ============================================================
REM verify-ansible.bat - Run playbook + verify results
REM Double-click or run from cmd.exe (not PowerShell)
REM ============================================================

echo.
echo ============================================================
echo  Step 1: Running Ansible Playbook
echo ============================================================
powershell -ExecutionPolicy Bypass -File ".\run-ansible.ps1"

echo.
echo ============================================================
echo  Step 2: Verifying MySQL Backup inside container
echo ============================================================
docker exec football-rental-webserver ls -lh /app/backups/

echo.
echo ============================================================
echo  Step 3: Verify Spring Boot is still responding
echo ============================================================
curl -s http://localhost:8080/api/terrains

echo.
echo ============================================================
echo  Step 4: Copy backups to your local machine
echo ============================================================
if not exist ".\backups" mkdir ".\backups"
docker cp football-rental-webserver:/app/backups/. .\backups\
echo Backups saved to: %CD%\backups\
dir .\backups\

echo.
echo ============================================================
echo  DONE
echo ============================================================
pause
