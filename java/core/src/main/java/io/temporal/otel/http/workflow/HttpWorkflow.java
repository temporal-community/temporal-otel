package io.temporal.otel.http.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface HttpWorkflow {
  @WorkflowMethod
  String httpGet(String url);
}
