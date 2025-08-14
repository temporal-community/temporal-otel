# Employee Service

## Overview

The `EmployeeService` is a Nexus RPC service that provides operations for retrieving employee information. This service follows the Nexus RPC protocol and is designed to handle employee data management operations within the Temporal workflow ecosystem.

## Operations

### getEmployees

Retrieves a list of employees based on the provided employee IDs.

#### Input Parameters

**Class:** `GetEmployeeInput`

| Field | Type | Description |
|-------|------|-------------|
| `employeeIds` | `List<String>` | List of employee IDs to retrieve |

**JSON Structure:**
```json
{
  "employeeIds": ["emp1", "emp2", "emp3"]
}
```

#### Output

**Class:** `GetEmployeeOutput`

| Field | Type | Description |
|-------|------|-------------|
| `employees` | `List<Employee>` | List of employee objects matching the requested IDs |

**JSON Structure:**
```json
{
  "employees": [
    {
      "id": "emp1",
      "name": "John Doe",
      "email": "john.doe@company.com"
    }
  ]
}
```

## Data Models

### Employee

The `Employee` class represents an individual employee entity.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Unique identifier for the employee |
| `name` | `String` | Full name of the employee |
| `email` | `String` | Email address of the employee |
