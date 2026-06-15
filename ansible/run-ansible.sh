#!/bin/bash
# =============================================================
# Quick-run script: Run the Ansible deployment playbook
# Usage: bash run-ansible.sh
# =============================================================

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}============================================================${NC}"
echo -e "${GREEN}  Football Rental - Ansible Deployment Runner${NC}"
echo -e "${GREEN}============================================================${NC}"
echo ""

# Check if ansible is installed
if ! command -v ansible-playbook &> /dev/null; then
    echo -e "${RED}❌ ansible-playbook is not installed.${NC}"
    echo ""
    echo "Install it on your control machine (NOT inside Docker) with:"
    echo "  Ubuntu/Debian : sudo apt install ansible"
    echo "  macOS         : brew install ansible"
    echo "  pip           : pip install ansible"
    echo ""
    exit 1
fi

echo -e "${YELLOW}🔧 Using Ansible version: $(ansible --version | head -1)${NC}"
echo ""

# Navigate to ansible directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo -e "${YELLOW}📋 Running playbook: deploy.yml${NC}"
echo -e "${YELLOW}📍 Inventory      : inventory/hosts.ini${NC}"
echo ""

# Run the playbook
ansible-playbook \
    -i inventory/hosts.ini \
    deploy.yml \
    -v

EXIT_CODE=$?

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}============================================================${NC}"
    echo -e "${GREEN}  ✅ Deployment completed successfully!${NC}"
    echo -e "${GREEN}============================================================${NC}"
else
    echo -e "${RED}============================================================${NC}"
    echo -e "${RED}  ❌ Deployment FAILED (exit code: $EXIT_CODE)${NC}"
    echo -e "${RED}============================================================${NC}"
fi

exit $EXIT_CODE
