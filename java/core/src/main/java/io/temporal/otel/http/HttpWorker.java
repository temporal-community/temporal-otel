package io.temporal.otel.http;

import io.temporal.client.WorkflowClient;
import io.temporal.opentracing.OpenTracingWorkerInterceptor;
import io.temporal.otel.http.workflow.HttpActivitiesImpl;
import io.temporal.otel.http.workflow.HttpWorkflowImpl;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;

public class HttpWorker {
  private static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
  private static final WorkflowClient client = WorkflowClient.newInstance(service);
  public static final String TASK_QUEUE_NAME = "otel-task-queue";

  public static void main(String[] args) {
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
