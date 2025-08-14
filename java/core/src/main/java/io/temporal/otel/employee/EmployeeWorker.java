package io.temporal.otel.employee;

import io.temporal.client.WorkflowClient;
import io.temporal.otel.http.Settings;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class EmployeeWorker {
  public static final String TASK_QUEUE_NAME = "employee-task-queue";

  public static void main(String[] args) {
    Settings settings = Settings.getInstance();

    WorkflowServiceStubsOptions stubOptions =
        WorkflowServiceStubsOptions.newBuilder().setTarget(settings.getTemporalHost()).build();
    WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubOptions);
    WorkflowClient client = WorkflowClient.newInstance(service);

    WorkerFactory factory = WorkerFactory.newInstance(client);

    Worker worker = factory.newWorker(TASK_QUEUE_NAME);
    worker.registerWorkflowImplementationTypes(EmployeeWorkflowImpl.class);
    worker.registerNexusServiceImplementation(new EmployeeServiceImpl());

    factory.start();
  }
}
