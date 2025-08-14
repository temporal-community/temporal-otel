package io.temporal.otel.notification;

import io.temporal.client.WorkflowClient;
import io.temporal.otel.options.ClientOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;
import io.temporal.workflow.NexusServiceOptions;
import java.util.Collections;

public class NotificationWorker {
  public static final String TASK_QUEUE_NAME = "notification-task-queue";

  public static void main(String[] args) {
    WorkflowClient client = ClientOptions.getWorkflowClient(args);

    WorkerFactory factory = WorkerFactory.newInstance(client);

    Worker worker = factory.newWorker(TASK_QUEUE_NAME);
    worker.registerWorkflowImplementationTypes(
        WorkflowImplementationOptions.newBuilder()
            .setNexusServiceOptions(
                Collections.singletonMap(
                    "EmployeeService",
                    NexusServiceOptions.newBuilder()
                        .setEndpoint("employee-nexus-endpoint")
                        .build()))
            .build(),
        NotificationWorkflowImpl.class);

    factory.start();
  }
}
