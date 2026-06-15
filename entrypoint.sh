#!/bin/bash
set -e

echo "=========================================="
echo "  Football Rental - Web Server Container  "
echo "=========================================="

# Start SSH daemon
echo "[INFO] Starting SSH daemon..."
service ssh start

# Wait for MySQL to be ready
echo "[INFO] Waiting for MySQL database to be ready..."
MAX_RETRIES=30
COUNT=0
until mysqladmin ping -h "mysql-db" -u root -pHello@123 --silent 2>/dev/null; do
    COUNT=$((COUNT + 1))
    if [ $COUNT -ge $MAX_RETRIES ]; then
        echo "[WARN] MySQL not reachable after $MAX_RETRIES attempts. Starting app anyway..."
        break
    fi
    echo "[INFO] MySQL not ready yet ($COUNT/$MAX_RETRIES). Waiting 3 seconds..."
    sleep 3
done

echo "[INFO] Starting Spring Boot application..."
java -jar /app/football-rental.jar &
SPRING_PID=$!

echo "[INFO] Waiting for Spring Boot to start on port 8080..."
sleep 15

echo "[INFO] Starting NGINX..."
nginx -g "daemon off;" &
NGINX_PID=$!

echo "[INFO] All services started!"
echo "  - Spring Boot PID: $SPRING_PID"
echo "  - NGINX PID:       $NGINX_PID"
echo "  - SSH:             running"

# Keep container alive
wait $SPRING_PID
