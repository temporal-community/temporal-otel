package io.temporal.otel.employee;

import io.temporal.otel.nexus.EmployeeService;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface EmployeeWorkflow {
  @WorkflowMethod
  EmployeeService.GetEmployeeOutput getEmployees(EmployeeService.GetEmployeeInput input);
}
