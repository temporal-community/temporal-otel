package io.temporal.otel.nexus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.nexusrpc.Operation;
import io.nexusrpc.Service;
import io.temporal.otel.employee.Employee;
import java.util.List;

@Service
public interface EmployeeService {

  class GetEmployeeInput {
    private final List<String> employeeIds;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public GetEmployeeInput(@JsonProperty("employeeIds") List<String> employeeIds) {
      this.employeeIds = employeeIds;
    }

    @JsonProperty("employeeIds")
    public List<String> getEmployeeIds() {
      return employeeIds;
    }
  }

  class GetEmployeeOutput {
    private final List<Employee> employees;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public GetEmployeeOutput(@JsonProperty("employees") List<Employee> employees) {
      this.employees = employees;
    }

    @JsonProperty("employees")
    public List<Employee> getEmployees() {
      return employees;
    }
  }

  @Operation
  GetEmployeeOutput getEmployees(GetEmployeeInput input);
}
