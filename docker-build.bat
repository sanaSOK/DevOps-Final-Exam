@echo off
cd /d "d:\I4\SERMISTER II\DevOps\football-rental\football-rental"
echo ============================================
echo  Building Docker Compose images...
echo ============================================
docker compose build --no-cache
echo.
echo Build exit code: %ERRORLEVEL%
