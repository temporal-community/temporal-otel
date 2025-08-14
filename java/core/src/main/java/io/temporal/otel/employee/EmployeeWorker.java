package io.temporal.otel.employee;

import io.temporal.client.WorkflowClient;
import io.temporal.otel.options.ClientOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class EmployeeWorker {
  public static final String TASK_QUEUE_NAME = "employee-task-queue";

  public static void main(String[] args) {
    WorkflowClient client = ClientOptions.getWorkflowClient(args);

    WorkerFactory factory = WorkerFactory.newInstance(client);

    Worker worker = factory.newWorker(TASK_QUEUE_NAME);
    worker.registerWorkflowImplementationTypes(EmployeeWorkflowImpl.class);
    worker.registerNexusServiceImplementation(new EmployeeServiceImpl());

    factory.start();
  }
}
