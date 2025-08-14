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

## Nexus demo

1. In a terminal, start Temporal dev server.

    ```bash
    temporal server start-dev
    ```

1. In a new terminal, create Temporal namespaces and Nexus endpoint.

    ```bash
    temporal operator namespace create --namespace employee-namespace
    temporal operator namespace create --namespace notification-namespace
    temporal operator nexus endpoint create \
        --name employee-nexus-endpoint \
        --target-namespace employee-namespace \
        --target-task-queue employee-task-queue \
        --description-file ./core/src/main/java/io/temporal/otel/nexus/EmployeeService.md
    ```

1. In a new terminal, start the Employee Worker.

    ```bash
    ./run_class.sh io.temporal.otel.employee.EmployeeWorker --args="-namespace employee-namespace"
    ```

1. In a new terminal, start the Notification Worker.

    ```bash
    ./run_class.sh io.temporal.otel.notification.NotificationWorker --args="-namespace notification-namespace"
    ```

1. In a new terminal, run the Notification Workflow.

    ```bash
    ./run_class.sh io.temporal.otel.notification.NotificationStarter --args="-namespace notification-namespace"
    ```
