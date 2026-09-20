# ==========================================
# Stage 1: Build the application using Maven
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and POM first for better layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Download dependencies offline (cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build runnable JAR
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Minimal runtime image with JRE
# ==========================================
FROM eclipse-temurin:21-jre-alpine

# Install curl for health check
RUN apk add --no-cache curl

# Create non-root user for enhanced security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

USER appuser

# Expose Spring Boot default port
EXPOSE 8080

# Configure healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/yte/login || exit 1

# Start the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
