#!/bin/bash

#
# Temporal Bootstrap Script
#
# This script sets up the required Temporal namespaces and Nexus endpoints
# for the temporal-otel project.
#
# Prerequisites:
# - Temporal CLI must be installed and available in PATH
# - Temporal server must be running and accessible
# - User must have appropriate permissions to create namespaces and endpoints
#

set -euo pipefail  # Exit on error, undefined vars, and pipe failures

# Script configuration
readonly SCRIPT_NAME="$(basename "$0")"
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Temporal configuration
readonly NEXUS_CALLER_NAMESPACE="notification-namespace"
readonly NEXUS_TARGET_NAMESPACE="employee-namespace"
readonly NEXUS_ENDPOINT_NAME="employee-nexus-endpoint"
readonly NEXUS_TARGET_TASK_QUEUE="employee-task-queue"
readonly NEXUS_DESCRIPTION_FILE="${PROJECT_ROOT}/java/core/src/main/java/io/temporal/otel/nexus/EmployeeService.md"

# Colors for output
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly NC='\033[0m' # No Color

# Logging functions
log() {
    local level="$1"
    shift
    local message="$*"
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')

    echo "[$timestamp] [$level] $message"
}

log_info() {
    echo -e "${BLUE}[INFO]${NC} $*"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $*"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $*"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $*" >&2
}

# Error handling
cleanup() {
    local exit_code=$?
    if [[ $exit_code -ne 0 ]]; then
        log_error "Script failed with exit code $exit_code"
    fi
    exit $exit_code
}

trap cleanup EXIT

# Help function
show_help() {
    cat << EOF
Usage: $SCRIPT_NAME [OPTIONS]

This script bootstraps Temporal namespaces and Nexus endpoints for the temporal-otel project.

OPTIONS:
    -h, --help              Show this help message
    --temporal-address     Temporal server address (default: localhost:7233)

EXAMPLES:
    $SCRIPT_NAME                                    # Use default values
    $SCRIPT_NAME --temporal-address prod:7233       # Use custom server

EOF
}

# Parse command line arguments
TEMPORAL_ADDRESS="localhost:7233"

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        --temporal-address)
            TEMPORAL_ADDRESS="$2"
            shift 2
            ;;
        *)
            log_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done



# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."

    # Check if temporal CLI is installed
    if ! command -v temporal &> /dev/null; then
        log_error "Temporal CLI is not installed or not in PATH"
        log_error "Please install it from: https://docs.temporal.io/cli"
        exit 1
    fi

    # Check if Nexus description file exists
    if [[ ! -f "$NEXUS_DESCRIPTION_FILE" ]]; then
        log_error "Nexus description file not found: $NEXUS_DESCRIPTION_FILE"
        exit 1
    fi
    log_info "Found Nexus description file: $NEXUS_DESCRIPTION_FILE"

    # Test Temporal server connectivity
    log_info "Testing connection to Temporal server at $TEMPORAL_ADDRESS..."
    if ! temporal operator cluster health --address "${TEMPORAL_ADDRESS}" &> /dev/null; then
        log_error "Cannot connect to Temporal server at ${TEMPORAL_ADDRESS}"
        log_error "Please ensure the Temporal server is running and accessible"
        exit 1
    fi

    log_success "All prerequisites satisfied"
}

# Check if namespace exists
namespace_exists() {
    local namespace="$1"
    temporal operator namespace describe --namespace "${namespace}" --address "${TEMPORAL_ADDRESS}" &> /dev/null
}

# Check if Nexus endpoint exists
nexus_endpoint_exists() {
    local endpoint_name="$1"
    temporal operator nexus endpoint get --name "${endpoint_name}" --address "${TEMPORAL_ADDRESS}" &> /dev/null
}

# Execute command
execute_command() {
    local cmd="$*"

    log_info "Executing: $cmd"

    if eval "$cmd"; then
        return 0
    else
        local exit_code=$?
        log_error "Command failed with exit code $exit_code: $cmd"
        return $exit_code
    fi
}

# Create namespace
create_namespace() {
    local namespace="$1"

    if namespace_exists "$namespace"; then
        log_warning "Namespace '$namespace' already exists, skipping creation"
        return 0
    fi

        log_info "Creating namespace: $namespace"
    execute_command "temporal operator namespace create --namespace '$namespace' --address '$TEMPORAL_ADDRESS'"

    # Verify creation
    if namespace_exists "$namespace"; then
        log_success "Successfully created namespace: $namespace"
    else
        log_error "Failed to verify namespace creation: $namespace"
        return 1
    fi
}

# Create Nexus endpoint
create_nexus_endpoint() {
    if nexus_endpoint_exists "$NEXUS_ENDPOINT_NAME"; then
        log_warning "Nexus endpoint '$NEXUS_ENDPOINT_NAME' already exists, skipping creation"
        return 0
    fi

        log_info "Creating Nexus endpoint: $NEXUS_ENDPOINT_NAME"
    execute_command "temporal operator nexus endpoint create \
        --name '$NEXUS_ENDPOINT_NAME' \
        --target-namespace '$NEXUS_TARGET_NAMESPACE' \
        --target-task-queue '$NEXUS_TARGET_TASK_QUEUE' \
        --description-file '$NEXUS_DESCRIPTION_FILE' \
        --address '$TEMPORAL_ADDRESS'"

    # Verify creation
    if nexus_endpoint_exists "$NEXUS_ENDPOINT_NAME"; then
        log_success "Successfully created Nexus endpoint: $NEXUS_ENDPOINT_NAME"
    else
        log_error "Failed to verify Nexus endpoint creation: $NEXUS_ENDPOINT_NAME"
        return 1
    fi
}

# Confirmation prompt
confirm_action() {
    echo
    echo "This script will create the following resources:"
    echo "  • Namespace: ${NEXUS_TARGET_NAMESPACE}"
    echo "  • Namespace: ${NEXUS_CALLER_NAMESPACE}"
    echo "  • Nexus Endpoint: ${NEXUS_ENDPOINT_NAME}"
    echo "    - Target Namespace: ${NEXUS_TARGET_NAMESPACE}"
    echo "    - Target Task Queue: ${NEXUS_TARGET_TASK_QUEUE}"
    echo "  • Temporal Server: ${TEMPORAL_ADDRESS}"
    echo

    read -p "Do you want to continue? (y/N): " -n 1 -r
    echo

    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "Operation cancelled by user"
        exit 0
    fi
}

# Main execution
main() {
    log_info "Starting Temporal bootstrap process..."

    check_prerequisites
    # confirm_action

    log_info "Creating namespaces..."
    create_namespace "${NEXUS_TARGET_NAMESPACE}"
    create_namespace "${NEXUS_CALLER_NAMESPACE}"

    log_info "Creating Nexus endpoint..."
    create_nexus_endpoint

    log_success "Temporal bootstrap completed successfully!"
    echo
    echo "Resources created:"
    echo "  ✓ Namespace: ${NEXUS_TARGET_NAMESPACE}"
    echo "  ✓ Namespace: ${NEXUS_CALLER_NAMESPACE}"
    echo "  ✓ Nexus Endpoint: ${NEXUS_ENDPOINT_NAME}"
}

# Run main function
main "$@"
