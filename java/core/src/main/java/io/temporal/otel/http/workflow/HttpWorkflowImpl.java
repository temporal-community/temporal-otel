package io.temporal.otel.http.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class HttpWorkflowImpl implements HttpWorkflow {
  private final HttpActivities activities =
      Workflow.newActivityStub(
          HttpActivities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());

  @Override
  public String httpGet(String url) {
    return activities.httpGet(url);
  }
}
