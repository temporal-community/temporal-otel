package io.temporal.otel.employee;

import io.nexusrpc.handler.OperationHandler;
import io.nexusrpc.handler.OperationImpl;
import io.nexusrpc.handler.ServiceImpl;
import io.temporal.client.WorkflowOptions;
import io.temporal.nexus.Nexus;
import io.temporal.nexus.WorkflowRunOperation;
import io.temporal.otel.nexus.EmployeeService;

@ServiceImpl(service = EmployeeService.class)
public class EmployeeServiceImpl {
  @OperationImpl
  public OperationHandler<EmployeeService.GetEmployeeInput, EmployeeService.GetEmployeeOutput>
      getEmployees() {
    return WorkflowRunOperation.fromWorkflowMethod(
        (ctx, details, input) ->
            Nexus.getOperationContext()
                    .getWorkflowClient()
                    .newWorkflowStub(
                        EmployeeWorkflow.class,
                        WorkflowOptions.newBuilder().setWorkflowId(details.getRequestId()).build())
                ::getEmployees);
  }
}
