package io.temporal.otel.http;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.otel.http.workflow.HttpWorkflow;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class HttpStarter {
  private static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
  private static final WorkflowClient client = WorkflowClient.newInstance(service);
  public static final String TASK_QUEUE_NAME = "otel-task-queue";
  public static final String WORKFLOW_ID = "http-workflow-java";

  public static void main(String[] args) {
    HttpWorkflow workflow =
        client.newWorkflowStub(
            HttpWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE_NAME)
                .build());

    String response = workflow.httpGet("https://httpbin.org/get");

    System.out.println(response);
  }
}
