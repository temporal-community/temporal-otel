package io.temporal.otel.http;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Sample Temporal Workflow Definition that executes a single Activity. */
public class OtelWorkflow {

  // Define the task queue name
  static final String TASK_QUEUE = "otel-task-queue";

  // Define our workflow unique id
  static final String WORKFLOW_ID = "http-workflow-java";

  @WorkflowInterface
  public interface HttpWorkflow {
    @WorkflowMethod
    String httpGet(String url);
  }

  @ActivityInterface
  public interface HttpActivities {
    @ActivityMethod(name = "http_get")
    String httpGet(String url);
  }

  public static class HttpActivitiesImpl implements HttpActivities {
    private static final Logger log = LoggerFactory.getLogger(HttpActivitiesImpl.class);

    @Override
    public String httpGet(String url) {
      log.info("Activity: making HTTP GET call to {}", url);

      try {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request =
            HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();

      } catch (IOException | InterruptedException e) {
        log.error("Error making HTTP GET request to {}: {}", url, e.getMessage());
        throw new RuntimeException("Failed to make HTTP GET request", e);
      }
    }
  }

  public static class HttpWorkflowImpl implements HttpWorkflow {
    private final HttpActivities activities =
        Workflow.newActivityStub(
            HttpActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());

    @Override
    public String httpGet(String url) {
      return activities.httpGet(url);
    }
  }

  public static void main(String[] args) {
    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    WorkflowClient client = WorkflowClient.newInstance(service);
    WorkerFactory factory = WorkerFactory.newInstance(client);
    Worker worker = factory.newWorker(TASK_QUEUE);

    worker.registerWorkflowImplementationTypes(HttpWorkflowImpl.class);
    worker.registerActivitiesImplementations(new HttpActivitiesImpl());

    factory.start();

    HttpWorkflow workflow =
        client.newWorkflowStub(
            HttpWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .build());

    String response = workflow.httpGet("https://httpbin.org/get");

    System.out.println(response);
    System.exit(0);
  }
}
