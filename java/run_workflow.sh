#!/bin/bash

# Script to run Java Temporal workflows using Gradle

set -e

# Check if workflow name is provided
if [ $# -eq 0 ]; then
    echo "Error: No fully qualified name of the workflow class provided"
    echo "Usage: $0 <workflow_fqn>"
    echo "Example: $0 io.temporal.samples.hello.HelloActivity"
    exit 1
fi

WORKFLOW_FQN=$1 # Fully qualified name of the workflow class

echo "Running workflow: ${WORKFLOW_FQN}"

# Run the workflow using gradle
./gradlew :core:execute -PmainClass="${WORKFLOW_FQN}"
