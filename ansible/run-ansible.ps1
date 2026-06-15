# =============================================================
# run-ansible.ps1 - Run Ansible playbook on Windows via Docker
# =============================================================
# Ansible does NOT run natively on Windows.
# This script launches a temporary Ansible Docker container that:
#   1. Joins the same Docker network as your containers
#   2. SSHes into CHOICE_A (football-rental-webserver) by container name
#   3. Runs the deploy.yml playbook
#
# Usage (from the ansible/ directory):
#   .\run-ansible.ps1
#   .\run-ansible.ps1 -DryRun     (check mode, no changes)
#   .\run-ansible.ps1 -Verbose    (extra verbose output)
# =============================================================

param(
    [string]$Playbook  = "deploy.yml",
    [string]$Inventory = "inventory/hosts.ini",
    [switch]$Verbose   = $false,
    [switch]$DryRun    = $false
)

# ---- Config --------------------------------------------------
# Custom local image: cytopia/ansible + openssh-client + sshpass
$ANSIBLE_IMAGE   = "football-rental-ansible:local"
$ANSIBLE_DIR_WIN = (Get-Location).Path

# Auto-detect the Docker network the webserver container is attached to
$DOCKER_NETWORK = ""
$inspectOutput = docker inspect football-rental-webserver 2>&1
if ($LASTEXITCODE -eq 0) {
    try {
        $inspectJson    = $inspectOutput | ConvertFrom-Json
        $DOCKER_NETWORK = ($inspectJson[0].NetworkSettings.Networks.PSObject.Properties | Select-Object -First 1).Name
    } catch {
        $DOCKER_NETWORK = ""
    }
}
if (-not $DOCKER_NETWORK) {
    $DOCKER_NETWORK = "football-rental_football-network"
}

# ---- Colors --------------------------------------------------
function Write-OK    { param($msg) Write-Host $msg -ForegroundColor Green  }
function Write-Warn  { param($msg) Write-Host $msg -ForegroundColor Yellow }
function Write-Fail  { param($msg) Write-Host $msg -ForegroundColor Red    }
function Write-Info  { param($msg) Write-Host $msg -ForegroundColor Cyan   }

# ---- Header --------------------------------------------------
Write-OK   "============================================================"
Write-OK   "  Football Rental - Ansible Runner (Docker-based)"
Write-OK   "============================================================"
Write-Warn "  Playbook  : $Playbook"
Write-Warn "  Inventory : $Inventory"
Write-Warn "  Network   : $DOCKER_NETWORK"
Write-Warn "  Image     : $ANSIBLE_IMAGE (custom, with SSH)"
Write-OK   "============================================================"
Write-Host ""

# ---- Check Docker is running ---------------------------------
Write-Info "[ ] Checking Docker is running..."
$dockerInfo = docker info 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Fail "[FAIL] Docker is not running! Please start Docker Desktop."
    exit 1
}
Write-OK "[OK] Docker is running"

# ---- Check the Docker network exists -------------------------
Write-Info "[ ] Checking Docker network '$DOCKER_NETWORK'..."
$networkExists = docker network inspect $DOCKER_NETWORK 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Fail "[FAIL] Docker network '$DOCKER_NETWORK' not found!"
    Write-Warn "       Make sure 'docker compose up -d' has been run first."
    Write-Warn "       Available networks:"
    docker network ls
    exit 1
}
Write-OK "[OK] Docker network found: $DOCKER_NETWORK"

# ---- Check the webserver container is running ----------------
Write-Info "[ ] Checking webserver container is running..."
$wsCheck = docker inspect football-rental-webserver 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Fail "[FAIL] Container 'football-rental-webserver' is not running!"
    Write-Warn "       Run: docker compose up -d"
    exit 1
}
Write-OK "[OK] Webserver container is running"
Write-Host ""

# ---- Build custom Ansible image (with openssh-client + sshpass) ----------
Write-Info "[ ] Building custom Ansible image '$ANSIBLE_IMAGE'..."
Write-Warn "    (Based on cytopia/ansible + openssh-client + sshpass)"
Write-Warn "    This is fast on subsequent runs thanks to Docker layer cache."
docker build -f Dockerfile.ansible -t $ANSIBLE_IMAGE .
if ($LASTEXITCODE -ne 0) {
    Write-Fail "[FAIL] Failed to build custom Ansible image!"
    exit 1
}
Write-OK "[OK] Custom Ansible image built: $ANSIBLE_IMAGE"
Write-Host ""

# ---- Build the ansible-playbook arguments --------------------
$ansibleArgs = @("-i", $Inventory, $Playbook)
if ($Verbose) { $ansibleArgs += "-v" }
if ($DryRun)  { $ansibleArgs += "--check" }

$argString = $ansibleArgs -join " "
Write-Info "[ ] Running Ansible playbook inside Docker..."
Write-Warn "    Command: ansible-playbook $argString"
Write-Host ""
Write-OK   "============================================================"
Write-Host ""

# ---- Run Ansible in Docker -----------------------------------
# Mounts the ansible/ directory into the container and joins the
# Docker network so it can reach football-rental-webserver by hostname.
docker run --rm `
    --network $DOCKER_NETWORK `
    -v "${ANSIBLE_DIR_WIN}:/ansible" `
    -w /ansible `
    -e "ANSIBLE_FORCE_COLOR=true" `
    -e "ANSIBLE_HOST_KEY_CHECKING=False" `
    $ANSIBLE_IMAGE `
    ansible-playbook @ansibleArgs

$exitCode = $LASTEXITCODE

# ---- Result --------------------------------------------------
Write-Host ""
Write-OK "============================================================"
if ($exitCode -eq 0) {
    Write-OK   "  DONE: Deployment completed successfully!"
} else {
    Write-Fail "  FAILED: Deployment failed (exit code: $exitCode)"
    Write-Warn "  Tip: Run with -Verbose switch for more detail."
}
Write-OK "============================================================"

exit $exitCode
