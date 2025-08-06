#!/bin/bash

# Script to run Java Temporal workflows using Gradle

set -e

# Check if workflow name is provided
if [ $# -eq 0 ]; then
    echo "Error: No fully qualified name of the Java class provided"
    echo "Usage: $0 <java_class_fqn>"
    echo "Example: $0 io.temporal.otel.http.HttpWorker"
    exit 1
fi

CLASS_FQN=$1 # Fully qualified name of the Java class

echo "Running Java class: ${CLASS_FQN}"

# Determine the correct path to gradlew based on script location
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"
./gradlew :core:execute -PmainClass="${CLASS_FQN}"
