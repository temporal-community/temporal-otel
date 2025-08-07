package io.temporal.otel.http;

import com.uber.m3.tally.RootScopeBuilder;
import com.uber.m3.tally.Scope;
import io.temporal.client.WorkflowClient;
import io.temporal.common.reporter.MicrometerClientStatsReporter;
import io.temporal.opentracing.OpenTracingWorkerInterceptor;
import io.temporal.otel.http.workflow.HttpActivitiesImpl;
import io.temporal.otel.http.workflow.HttpWorkflowImpl;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;

public class HttpWorker {
  public static final String TASK_QUEUE_NAME = "otel-task-queue";

  public static void main(String[] args) {
    // Set OpenTelemetry metrics scope for the Worker
    Scope scope =
        new RootScopeBuilder()
            .reporter(new MicrometerClientStatsReporter(MetricsUtils.getMeterRegistry()))
            .reportEvery(com.uber.m3.util.Duration.ofSeconds(1));
    WorkflowServiceStubsOptions stubOptions =
        WorkflowServiceStubsOptions.newBuilder().setMetricsScope(scope).build();
    WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubOptions);
    WorkflowClient client = WorkflowClient.newInstance(service);

    // Set OpenTelemetry tracer for the Worker
    WorkerFactoryOptions factoryOptions =
        WorkerFactoryOptions.newBuilder()
            .setWorkerInterceptors(new OpenTracingWorkerInterceptor(TraceUtils.getTraceOptions()))
            .build();
    WorkerFactory factory = WorkerFactory.newInstance(client, factoryOptions);
    Worker worker = factory.newWorker(TASK_QUEUE_NAME);

    worker.registerWorkflowImplementationTypes(HttpWorkflowImpl.class);
    worker.registerActivitiesImplementations(new HttpActivitiesImpl());

    factory.start();
  }
}
