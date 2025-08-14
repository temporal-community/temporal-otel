package io.temporal.otel.notification;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface NotificationWorkflow {
  @WorkflowMethod
  void notifyAllEmployees();
}
