package io.temporal.otel.employee;

import io.temporal.otel.nexus.EmployeeService;
import java.util.List;

public class EmployeeWorkflowImpl implements EmployeeWorkflow {
  @Override
  public EmployeeService.GetEmployeeOutput getEmployees(EmployeeService.GetEmployeeInput input) {
    List<Employee> employees;
    if (input.getEmployeeIds() == null || input.getEmployeeIds().isEmpty()) {
      employees = EmployeeDatabase.getAllEmployees();
    } else {
      employees = EmployeeDatabase.getEmployeesByIds(input.getEmployeeIds());
    }
    return new EmployeeService.GetEmployeeOutput(employees);
  }
}
