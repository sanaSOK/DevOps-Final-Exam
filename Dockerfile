# ============================================================
# Stage 1: Build the Spring Boot JAR using JDK 25
# ============================================================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /build

# Copy Maven wrapper and POM first (for layer caching)
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn/ .mvn/

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ============================================================
# Stage 2: Runtime container with JDK 25 + NGINX + SSH
# ============================================================
FROM eclipse-temurin:25-jdk

LABEL maintainer="SANA"
LABEL description="Football Rental - Spring Boot + NGINX + SSH (JDK 25)"

# Install NGINX, OpenSSH server, and MySQL client (for health check)
RUN apt-get update && apt-get install -y \
    nginx \
    openssh-server \
    default-mysql-client \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Configure SSH
RUN mkdir -p /var/run/sshd \
    && echo 'root:rootpassword' | chpasswd \
    && sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config \
    && sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config

# Copy the built JAR from Stage 1
COPY --from=builder /build/target/*.jar /app/football-rental.jar

# Copy NGINX configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Expose ports:
#   80  - NGINX (proxies to Spring Boot on 8080)
#   8080 - Spring Boot directly
#   22  - SSH
EXPOSE 80 8080 22

ENTRYPOINT ["/entrypoint.sh"]
