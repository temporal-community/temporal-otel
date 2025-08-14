package io.temporal.otel.employee;

import java.util.Objects;

public class Employee {
  private String id;
  private String name;
  private String email;

  // Default constructor for Jackson deserialization
  public Employee() {}

  public Employee(String id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  // Getters
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  // Setters for Jackson deserialization
  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email);
  }

  @Override
  public String toString() {
    return "Employee{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", email='"
        + email
        + '\''
        + '}';
  }
}
