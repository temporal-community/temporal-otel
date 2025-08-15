package io.temporal.otel.notification;

import io.temporal.client.WorkflowClient;
import io.temporal.opentracing.OpenTracingWorkerInterceptor;
import io.temporal.otel.options.ClientOptions;
import io.temporal.otel.utils.Settings;
import io.temporal.otel.utils.TraceUtils;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkflowImplementationOptions;
import io.temporal.workflow.NexusServiceOptions;
import java.util.Collections;

public class NotificationWorker {
  public static final String TASK_QUEUE_NAME = "notification-task-queue";

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
