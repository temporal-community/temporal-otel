package io.temporal.otel.http.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface HttpActivities {
  @ActivityMethod(name = "http_get")
  String httpGet(String url);
}
