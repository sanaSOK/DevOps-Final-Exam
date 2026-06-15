# ============================================================
# Football Rental - Git Setup & GitHub Push Script
# ============================================================
# Run this script in PowerShell from any location.
# Prerequisites:
#   1. Git is installed
#   2. GitHub CLI (gh) is installed and authenticated: gh auth login
#   3. OR manually create the repo on github.com first
# ============================================================

$PROJECT_DIR = "d:\I4\SERMISTER II\DevOps\football-rental\football-rental"
$GITHUB_REPO_NAME = "football-rental"
$GITHUB_COLLABORATOR = "srengty"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Football Rental - GitHub Setup Script  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Change to project directory
Set-Location $PROJECT_DIR
Write-Host "`n[1/6] Changed directory to: $PROJECT_DIR" -ForegroundColor Green

# Initialize git repository
if (-Not (Test-Path ".git")) {
    git init
    Write-Host "[2/6] Git repository initialized." -ForegroundColor Green
} else {
    Write-Host "[2/6] Git repository already initialized. Skipping..." -ForegroundColor Yellow
}

# Configure git user (update these if not already configured globally)
# git config user.name "Your Name"
# git config user.email "your@email.com"

# Stage all files
git add .
Write-Host "[3/6] Staged all files." -ForegroundColor Green

# Create initial commit
git commit -m "feat: initial Spring Boot project setup with models, repositories, controllers, and seeder

- Models: Terrain, TerrainImage, Booking, Payment, Review, Favorite
- Repositories: Spring Data JPA repositories for all models
- Controllers: Full CRUD REST API controllers
- Seeder: DataSeeder with sample data
- Database: MySQL configuration"
Write-Host "[4/6] Created initial commit." -ForegroundColor Green

# ──────────────────────────────────────────────────────────
# OPTION A: Use GitHub CLI to create the repository
# ──────────────────────────────────────────────────────────
Write-Host "`n[5/6] Creating GitHub repository..." -ForegroundColor Green
gh repo create $GITHUB_REPO_NAME --public --source=. --remote=origin --push

# ──────────────────────────────────────────────────────────
# OPTION B: If you already created the repo on GitHub manually,
# comment out OPTION A above and uncomment these lines:
# ──────────────────────────────────────────────────────────
# $GITHUB_USERNAME = "YOUR_GITHUB_USERNAME"
# git remote add origin "https://github.com/$GITHUB_USERNAME/$GITHUB_REPO_NAME.git"
# git branch -M main
# git push -u origin main

# ──────────────────────────────────────────────────────────
# Add collaborator
# ──────────────────────────────────────────────────────────
Write-Host "[6/6] Adding collaborator: $GITHUB_COLLABORATOR..." -ForegroundColor Green
gh repo add-collaborator $GITHUB_COLLABORATOR

Write-Host "`n✅ Done! Your project is now on GitHub." -ForegroundColor Cyan
Write-Host "   Collaborator '$GITHUB_COLLABORATOR' has been invited." -ForegroundColor Cyan
Write-Host "`n⚠️  Remember to update application.properties with your MySQL credentials!" -ForegroundColor Yellow
