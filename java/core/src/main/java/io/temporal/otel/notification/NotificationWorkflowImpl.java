package io.temporal.otel.notification;

import io.temporal.otel.nexus.EmployeeService;
import io.temporal.workflow.NexusOperationHandle;
import io.temporal.workflow.NexusOperationOptions;
import io.temporal.workflow.NexusServiceOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.Arrays;
import org.slf4j.Logger;

public class NotificationWorkflowImpl implements NotificationWorkflow {
  EmployeeService employeeService =
      Workflow.newNexusServiceStub(
          EmployeeService.class,
          NexusServiceOptions.newBuilder()
              .setOperationOptions(
                  NexusOperationOptions.newBuilder()
                      .setScheduleToCloseTimeout(Duration.ofSeconds(5))
                      .build())
              .build());
  private static final Logger logger = Workflow.getLogger(NotificationWorkflowImpl.class);

  @Override
  public void notifyAllEmployees() {
    NexusOperationHandle<EmployeeService.GetEmployeeOutput> handle =
        Workflow.startNexusOperation(
            employeeService::getEmployees, new EmployeeService.GetEmployeeInput(Arrays.asList()));
    handle.getExecution().get();

    logger.info("Employees: {}", handle.getResult().get().getEmployees().toString());
  }
}
