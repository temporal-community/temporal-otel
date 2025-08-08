#!/bin/bash

# Temporal OpenTelemetry Demo Startup Script
# This script starts the Temporal OpenTelemetry demo with specified SDK and platform configurations.
#
# Usage: ./start.sh --sdk <python|java> --platform <oss|dynatrace>
#
# Requirements:
# - Docker and Docker Compose must be installed
# - For Dynatrace platform: DYNATRACE_TENANT and DYNATRACE_API_TOKEN environment variables

set -euo pipefail  # Exit on error, undefined vars, and pipe failures

# Default values
DEFAULT_SDK="python"
DEFAULT_PLATFORM="oss"

# Global variables
SDK=""
PLATFORM=""
VERBOSE=false
HELP=false
DETACHED=false
REBUILD=false

# Color codes for output
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1" >&2
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1" >&2
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" >&2
}

# Show usage information
show_usage() {
    cat << EOF
Temporal OpenTelemetry Demo Startup Script

USAGE:
    $0 --sdk <SDK> --platform <PLATFORM> [OPTIONS]

REQUIRED ARGUMENTS:
    --sdk <SDK>           Temporal SDK to use (python or java)
    --platform <PLATFORM> Observability platform (oss or dynatrace)

OPTIONS:
    -h, --help           Show this help message and exit
    -v, --verbose        Enable verbose output
    -d, --detached       Run containers in detached mode
    -r, --rebuild        Force rebuild of containers
    --down               Stop and remove all containers
    --logs [SERVICE]     Show logs for all services or specific service

SUPPORTED VALUES:
    SDK: python, java
    PLATFORM: oss, dynatrace

EXAMPLES:
    $0 --sdk python --platform oss
    $0 --sdk java --platform dynatrace --detached
    $0 --sdk python --platform oss --rebuild --verbose
    $0 --down
    $0 --logs worker-oss

ENVIRONMENT VARIABLES (for Dynatrace):
    DYNATRACE_TENANT     Your Dynatrace tenant ID (required for --platform dynatrace)
    DYNATRACE_API_TOKEN  Your Dynatrace API token (required for --platform dynatrace)

EOF
}

# Parse command line arguments
parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --sdk)
                SDK="$2"
                shift 2
                ;;
            --platform)
                PLATFORM="$2"
                shift 2
                ;;
            -h|--help)
                HELP=true
                shift
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            -d|--detached)
                DETACHED=true
                shift
                ;;
            -r|--rebuild)
                REBUILD=true
                shift
                ;;
            --down)
                docker_compose_down
                exit 0
                ;;
            --logs)
                if [[ $# -gt 1 && ! $2 =~ ^-- ]]; then
                    show_logs "$2"
                    shift 2
                else
                    show_logs
                    shift
                fi
                exit 0
                ;;
            *)
                log_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
}

# Validate arguments
validate_arguments() {
    local valid_sdks=("python" "java")
    local valid_platforms=("oss" "dynatrace")

    # Check if SDK is provided and valid
    if [[ -z "$SDK" ]]; then
        log_error "SDK argument is required. Use --sdk <python|java>"
        exit 1
    fi

    if [[ ! " ${valid_sdks[*]} " =~ " ${SDK} " ]]; then
        log_error "Invalid SDK: $SDK. Supported values: ${valid_sdks[*]}"
        exit 1
    fi

    # Check if PLATFORM is provided and valid
    if [[ -z "$PLATFORM" ]]; then
        log_error "Platform argument is required. Use --platform <oss|dynatrace>"
        exit 1
    fi

    if [[ ! " ${valid_platforms[*]} " =~ " ${PLATFORM} " ]]; then
        log_error "Invalid platform: $PLATFORM. Supported values: ${valid_platforms[*]}"
        exit 1
    fi
}

# Check system prerequisites
check_prerequisites() {
    local missing_deps=()

    # Check for Docker
    if ! command -v docker &> /dev/null; then
        missing_deps+=("docker")
    fi

    # Check for Docker Compose
    if ! command -v docker &> /dev/null || ! docker compose version &> /dev/null; then
        missing_deps+=("docker-compose")
    fi

    if [[ ${#missing_deps[@]} -gt 0 ]]; then
        log_error "Missing required dependencies: ${missing_deps[*]}"
        log_error "Please install Docker and Docker Compose before running this script."
        exit 1
    fi

    # Check Docker daemon
    if ! docker info &> /dev/null; then
        log_error "Docker daemon is not running. Please start Docker and try again."
        exit 1
    fi
}

# Validate environment for Dynatrace
validate_dynatrace_env() {
    if [[ "$PLATFORM" == "dynatrace" ]]; then
        local missing_vars=()

        if [[ -z "${DYNATRACE_TENANT:-}" ]]; then
            missing_vars+=("DYNATRACE_TENANT")
        fi

        if [[ -z "${DYNATRACE_API_TOKEN:-}" ]]; then
            missing_vars+=("DYNATRACE_API_TOKEN")
        fi

        if [[ ${#missing_vars[@]} -gt 0 ]]; then
            log_error "Dynatrace platform requires the following environment variables:"
            for var in "${missing_vars[@]}"; do
                log_error "  $var"
            done
            log_error "Please set these variables and try again."
            exit 1
        fi

        log_info "Dynatrace configuration validated"
    fi
}

# Stop and remove containers
docker_compose_down() {
    log_info "Stopping and removing containers..."

    if docker compose ps --quiet | grep -q .; then
        docker compose --profile oss --profile dynatrace down --volumes --remove-orphans
        log_success "Containers stopped and removed"
    else
        log_info "No containers are currently running"
    fi
}

# Show logs
show_logs() {
    local service="${1:-}"

    if [[ -n "$service" ]]; then
        log_info "Showing logs for service: $service"
        docker compose --profile oss --profile dynatrace logs --follow "$service"
    else
        log_info "Showing logs for all services"
        docker compose --profile oss --profile dynatrace logs --follow
    fi
}

# Start services with Docker Compose
start_services() {
    local compose_args=("--profile" "${PLATFORM}" "up")

    if [[ "$DETACHED" == true ]]; then
        compose_args+=("--detach")
    fi

    if [[ "$REBUILD" == true ]]; then
        compose_args+=("--build")
    fi

    log_info "Starting services with the following configuration:"
    log_info "  SDK: $SDK"
    log_info "  Platform: $PLATFORM"
    log_info "  Profile: $PLATFORM"

    if [[ "$VERBOSE" == true ]]; then
        log_info "  Detached: $DETACHED"
        log_info "  Rebuild: $REBUILD"
    fi

    # Export SDK as environment variable for docker-compose
    export SDK

    if [[ "$VERBOSE" == true ]]; then
        set -x  # Enable command tracing in verbose mode
    fi

    # Start the services
    if docker compose "${compose_args[@]}"; then
        log_success "Services started successfully!"

        if [[ "$DETACHED" == true ]]; then
            log_info "Services are running in detached mode."
            log_info "Use '$0 --logs' to view logs"
            log_info "Use '$0 --down' to stop services"
        fi

        show_service_urls
    else
        log_error "Failed to start services"
        exit 1
    fi

    if [[ "$VERBOSE" == true ]]; then
        set +x  # Disable command tracing
    fi
}

# Show URLs for running services
show_service_urls() {
    log_info "Service URLs:"
    log_info "  Temporal Web UI: http://localhost:8233"

    if [[ "$PLATFORM" == "oss" ]]; then
        log_info "  Jaeger UI: http://localhost:16686"
        log_info "  Prometheus UI: http://localhost:9090"
        log_info "  Elasticsearch: http://localhost:9200"
    fi

    log_info "  OpenTelemetry Collector Metrics: http://localhost:8889/metrics"
}

# Cleanup function for graceful exit
cleanup() {
    local exit_code=$?
    if [[ $exit_code -ne 0 ]]; then
        log_error "Script execution failed with exit code $exit_code"
    fi
    exit $exit_code
}

# Set trap for cleanup
trap cleanup EXIT

# Main execution flow
main() {
    # Parse command line arguments
    parse_arguments "$@"

    # Show help if requested
    if [[ "$HELP" == true ]]; then
        show_usage
        exit 0
    fi

    # Validate arguments
    validate_arguments

    # Check system prerequisites
    check_prerequisites

    # Validate Dynatrace environment if needed
    validate_dynatrace_env

    # Start services
    start_services
}

# Run main function with all arguments
main "$@"
