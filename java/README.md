# Java Workflow

## Pre-requisites

- [sdkman](https://sdkman.io/install/)
- [Java JDK](https://sdkman.io/jdks)

## Getting started

To run a Java workflow, run the following command in your terminal.

    ```bash
    ./run_workflow.sh <fully-qualified-workflow-name-in-core>
    ```

For example, you would use the following command to run the [`OtelWorkflow`](./core/src/main/java/io/temporal/otel/http/OtelWorkflow.java).

    ```bash
    ./run_workflow.sh io.temporal.otel.http.OtelWorkflow
    ```
