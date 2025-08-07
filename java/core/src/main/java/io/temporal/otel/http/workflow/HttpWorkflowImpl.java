package io.temporal.otel.http.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpWorkflowImpl implements HttpWorkflow {
  private final HttpActivities activities =
      Workflow.newActivityStub(
          HttpActivities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());
  private static final Logger log = LoggerFactory.getLogger(HttpWorkflowImpl.class);

  @Override
  public String httpGet(String url) {
    log.info("Workflow: making HTTP GET call to {}", url);
    return activities.httpGet(url);
  }
}
