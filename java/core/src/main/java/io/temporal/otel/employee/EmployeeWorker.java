package io.temporal.otel.employee;

import io.temporal.client.WorkflowClient;
import io.temporal.opentracing.OpenTracingWorkerInterceptor;
import io.temporal.otel.options.ClientOptions;
import io.temporal.otel.utils.Settings;
import io.temporal.otel.utils.TraceUtils;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;

public class EmployeeWorker {
  public static final String TASK_QUEUE_NAME = "employee-task-queue";

  public static void main(String[] args) {
    Settings settings = Settings.getInstance();
    WorkflowClient client = ClientOptions.getWorkflowClient(args);

    // Set OpenTelemetry tracer for the Worker
    WorkerFactoryOptions factoryOptions =
        WorkerFactoryOptions.newBuilder()
            .setWorkerInterceptors(
                new OpenTracingWorkerInterceptor(
                    TraceUtils.getTraceOptions(settings.getOtlpEndpoint())))
            .build();
    WorkerFactory factory = WorkerFactory.newInstance(client, factoryOptions);
    Worker worker = factory.newWorker(TASK_QUEUE_NAME);

    worker.registerWorkflowImplementationTypes(EmployeeWorkflowImpl.class);
    worker.registerNexusServiceImplementation(new EmployeeServiceImpl());

    factory.start();
  }
}
