# Java Workflow

## Pre-requisites

- [sdkman](https://sdkman.io/install/)
- [Java JDK](https://sdkman.io/jdks)

## Getting started

1. In a terminal, start Temporal dev server.

    ```bash
    temporal server start-dev
    ```

1. In a new terminal, run the Worker.

    ```bash
    ./run_class.sh io.temporal.otel.http.HttpWorker
    ```

1. In a new terminal, run the Starter.

    ```bash
    ./run_class.sh io.temporal.otel.http.HttpStarter
    ```

## Nexus sample

1. In a terminal, start Docker compose without running the Workers.

    ```bash
    docker compose --profile oss up --build --scale worker-oss=0
    ```

1. In a new terminal, create Temporal namespaces and Nexus endpoint.

    ```bash
    ./tools/bootstrap_temporal.sh
    ```

1. In a new terminal, start the Employee Worker.

    ```bash
    ./run_class.sh io.temporal.otel.employee.EmployeeWorker --args="-namespace employee-namespace -otlp-endpoint http://localhost:4317"
    ```

1. In a new terminal, start the Notification Worker.

    ```bash
    ./run_class.sh io.temporal.otel.notification.NotificationWorker --args="-namespace notification-namespace -otlp-endpoint http://localhost:4317"
    ```

1. In a new terminal, run the Notification Workflow.

    ```bash
    ./run_class.sh io.temporal.otel.notification.NotificationStarter --args="-namespace notification-namespace -otlp-endpoint http://localhost:4317"
    ```
