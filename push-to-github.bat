@echo off
echo =====================================================
echo  Football Rental - Push to GitHub
echo  Repository: https://github.com/sanaSOK/DevOps-Final-Exam.git
echo =====================================================
echo.

cd /d "d:\I4\SERMISTER II\DevOps\football-rental\football-rental"
if %errorlevel% neq 0 (
    echo ERROR: Could not navigate to project directory.
    pause
    exit /b 1
)

echo [1/6] Initializing git repository...
git init
if %errorlevel% neq 0 (echo WARNING: git init had an issue, continuing... )

echo.
echo [2/6] Staging all files...
git add .

echo.
echo [3/6] Creating initial commit...
git commit -m "feat: initial Spring Boot project setup

- Models: Terrain, TerrainImage, Booking, Payment, Review, Favorite
- Repositories: Spring Data JPA with custom queries
- Controllers: Full CRUD REST API for all models
- Seeder: DataSeeder with sample football field data
- Config: MySQL database configuration"

echo.
echo [4/6] Setting remote to GitHub repository...
git remote remove origin 2>nul
git remote add origin https://github.com/sanaSOK/DevOps-Final-Exam.git

echo.
echo [5/6] Setting default branch to main...
git branch -M main

echo.
echo [6/6] Pushing to GitHub...
echo NOTE: You may be asked to log in with your GitHub credentials.
git push -u origin main

echo.
echo =====================================================
if %errorlevel% equ 0 (
    echo  SUCCESS! Code pushed to GitHub.
    echo  https://github.com/sanaSOK/DevOps-Final-Exam
) else (
    echo  Push failed. Try running: git push -u origin main
)
echo =====================================================
pause
