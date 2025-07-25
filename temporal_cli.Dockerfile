# Use Alpine Linux as the base image for minimal size
FROM alpine:3.19

# Add metadata labels
LABEL maintainer="Ka Wo Fong" \
      description="Temporal CLI on Alpine Linux" \
      version="1.0"

# Set Temporal CLI version (can be overridden at build time)
ARG TEMPORAL_CLI_VERSION=1.4.1
ARG TARGETPLATFORM=linux/amd64

# Install necessary packages
RUN apk add --no-cache \
    ca-certificates \
    curl \
    && rm -rf /var/cache/apk/*

# Create a non-root user for security
RUN addgroup -g 1001 temporal && \
    adduser -D -u 1001 -G temporal temporal

# Set working directory
WORKDIR /opt/temporal

# Download and install Temporal CLI
RUN set -eux; \
    case "${TARGETPLATFORM}" in \
        "linux/amd64") ARCH="amd64" ;; \
        "linux/arm64") ARCH="arm64" ;; \
        *) echo "Unsupported platform: ${TARGETPLATFORM}" && exit 1 ;; \
    esac; \
    curl -fsSL "https://github.com/temporalio/cli/releases/download/v${TEMPORAL_CLI_VERSION}/temporal_cli_${TEMPORAL_CLI_VERSION}_linux_${ARCH}.tar.gz" \
        -o temporal_cli.tar.gz && \
    tar -xzf temporal_cli.tar.gz && \
    mv temporal /usr/local/bin/temporal && \
    chmod +x /usr/local/bin/temporal && \
    rm -f temporal_cli.tar.gz

# Switch to non-root user
USER temporal

# Verify installation
RUN temporal --version

# Set the default command
ENTRYPOINT ["temporal"]
CMD ["--help"]
