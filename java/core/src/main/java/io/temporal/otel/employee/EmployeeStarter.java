package io.temporal.otel.employee;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.otel.nexus.EmployeeService;
import io.temporal.otel.options.ClientOptions;
import java.util.Arrays;

public class EmployeeStarter {
  public static final String TASK_QUEUE_NAME = "employee-task-queue";
  public static final String WORKFLOW_ID = "employee-workflow-java";

  public static void main(String[] args) {
    WorkflowClient client = ClientOptions.getWorkflowClient(args);

    EmployeeWorkflow workflow =
        client.newWorkflowStub(
            EmployeeWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE_NAME)
                .build());

    EmployeeService.GetEmployeeOutput response =
        workflow.getEmployees(new EmployeeService.GetEmployeeInput(Arrays.asList()));

    System.out.println(response.getEmployees().toString());
  }
}
