# Ansible Deployment — Football Rental

## Overview

This Ansible playbook automates the full deployment cycle on the **Web Server (CHOICE_A)**, which is the `webserver` Docker container accessible via SSH (port 22).

---

## 📁 Directory Structure

```
ansible/
├── ansible.cfg              ← Ansible configuration
├── deploy.yml               ← Main playbook (all 5 steps)
├── run-ansible.sh           ← Helper script to run playbook
└── inventory/
    └── hosts.ini            ← Inventory: CHOICE_A = webserver container
```

---

## ✅ What the Playbook Does

| Step | Action | Details |
|------|--------|---------|
| **0** | 🔌 SSH Login to CHOICE_A | Verifies SSH connection to the webserver container |
| **1** | 📥 Git Pull | Resets local changes (`git reset --hard`) then runs `git pull` |
| **2** | 🏗️ Build (Maven) | Runs `./mvnw dependency:go-offline` (like *composer install*) then `./mvnw package` (like *npm run build*) |
| **3** | 🧪 Tests (SQLite) | Runs `./mvnw test -Dspring.profiles.active=test` using **SQLite in-memory** (no MySQL needed) |
| **4** | 💾 MySQL Backup | Runs `mysqldump` on the production `SANA-db` MySQL database |
| **5** | 🚀 Restart App | Kills old Spring Boot process, deploys new JAR, waits for health check |

---

## 🗄️ Database Strategy

| Context | Database | Config |
|---------|----------|--------|
| **Production** | MySQL (`SANA-db` on `mysql-db:3306`) | `application.properties` |
| **Tests** | SQLite (in-memory) | `application-test.properties` (activated via `spring.profiles.active=test`) |

---

## 🚀 How to Run

### ✅ On Windows (PowerShell) — Recommended

Ansible does **not** run natively on Windows. Use the included PowerShell wrapper which spins up a temporary Ansible Docker container:

```powershell
# From the ansible/ directory:
cd ansible
.\run-ansible.ps1
```

This script automatically:
- Pulls the `cytopia/ansible` Docker image
- Attaches it to the same Docker network as your containers
- SSHes into `CHOICE_A` (football-rental-webserver) by container name
- Runs `deploy.yml` and shows colored output

> **Prerequisites**: Docker Desktop must be running and `docker compose up -d` must have been executed first.

---

### On Linux / macOS / WSL

Install Ansible natively then run:

```bash
# Install Ansible
sudo apt install ansible   # Ubuntu/Debian
brew install ansible       # macOS

# Run the playbook
cd ansible
ansible-playbook -i inventory/hosts.ini deploy.yml -v
```

---

## 🔧 Configuration

Edit `inventory/hosts.ini` to change:
- `ansible_host` — IP/hostname of the webserver
- `ansible_port` — SSH port (default: 22)
- `ansible_password` — SSH password (default: `rootpassword`)
- `mysql_password` — MySQL root password (default: `Hello@123`)

---

## 📦 MySQL Backup Files

Backups are stored inside the container at `/app/backups/`:
```
/app/backups/SANA-db_backup_20260615T162000.sql
```

To copy a backup to your host machine:
```bash
docker cp football-rental-webserver:/app/backups/ ./backups/
```
