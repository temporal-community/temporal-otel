package io.temporal.otel.notification;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.otel.options.ClientOptions;

public class NotificationStarter {
  public static final String TASK_QUEUE_NAME = "notification-task-queue";
  public static final String WORKFLOW_ID = "notification-workflow-java";

  public static void main(String[] args) {
    WorkflowClient client = ClientOptions.getWorkflowClient(args);

    NotificationWorkflow workflow =
        client.newWorkflowStub(
            NotificationWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE_NAME)
                .build());

    workflow.notifyAllEmployees();
  }
}
