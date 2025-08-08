# Multi-stage build for Java Temporal Worker
# Build stage
FROM gradle:8.14.3-jdk17-ubi-minimal AS builder

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and configuration files for dependency caching
COPY java/gradle gradle/
COPY java/gradlew java/gradlew.bat java/build.gradle java/settings.gradle java/gradle.properties ./
COPY java/core/build.gradle core/

# Download dependencies first for better layer caching
RUN ./gradlew --no-daemon dependencies

# Copy source code
COPY java/core/src core/src
COPY java/core/bin core/bin

# Build the application and create a distribution
RUN ./gradlew --no-daemon :core:build -x test

# Copy runtime dependencies to a dedicated directory
RUN ./gradlew --no-daemon :core:copyRuntimeDependencies

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

# Install system dependencies and security updates
RUN apt-get update && apt-get install -y \
    --no-install-recommends \
    ca-certificates \
    curl \
    dumb-init \
    && apt-get upgrade -y \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Create non-root user for security
RUN groupadd --gid 1000 worker && \
    useradd --uid 1000 --gid worker --shell /bin/bash --create-home worker

# Set working directory
WORKDIR /app

# Copy built application and dependencies
COPY --from=builder --chown=worker:worker /app/core/build/classes/java/main/ ./classes/
COPY --from=builder --chown=worker:worker /app/core/build/resources/main/ ./resources/

# Copy runtime dependencies
RUN mkdir -p /app/lib
COPY --from=builder --chown=worker:worker /app/core/build/deps/ /app/lib/

# Create final classpath
RUN echo "/app/classes:/app/resources:/app/lib/*" > /app/classpath.txt && \
    chown worker:worker /app/classpath.txt

# Switch to non-root user
USER worker

# Set environment variables
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication" \
    TEMPORAL_HOST=temporal:7233 \
    OTLP_ENDPOINT=http://otel-collector:4317

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]

# Run the worker
CMD ["sh", "-c", "java $JAVA_OPTS -cp $(cat /app/classpath.txt) io.temporal.otel.http.HttpWorker"]
