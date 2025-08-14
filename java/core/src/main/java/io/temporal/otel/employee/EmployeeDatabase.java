package io.temporal.otel.employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeDatabase {
  private static final Logger log = LoggerFactory.getLogger(EmployeeDatabase.class);
  private static final String DB_URL = "jdbc:sqlite:employees.db";
  private static final String CREATE_TABLE_SQL =
      "CREATE TABLE IF NOT EXISTS employees ("
          + "id TEXT PRIMARY KEY, "
          + "name TEXT NOT NULL, "
          + "email TEXT NOT NULL"
          + ")";

  private static final String INSERT_SAMPLE_DATA_SQL =
      "INSERT OR IGNORE INTO employees (id, name, email) VALUES "
          + "('1', 'John Doe', 'john.doe@example.com'), "
          + "('2', 'Jane Smith', 'jane.smith@example.com'), "
          + "('3', 'Bob Johnson', 'bob.johnson@example.com')";

  static {
    initializeDatabase();
  }

  private static void initializeDatabase() {
    try (Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement()) {

      // Create table
      stmt.execute(CREATE_TABLE_SQL);
      log.info("Employee table created or already exists");

      // Insert sample data
      stmt.execute(INSERT_SAMPLE_DATA_SQL);
      log.info("Sample employee data inserted");

    } catch (SQLException e) {
      log.error("Failed to initialize database", e);
      throw new RuntimeException("Database initialization failed", e);
    }
  }

  public static List<Employee> getEmployeesByIds(List<String> employeeIds) {
    List<Employee> employees = new ArrayList<>();

    if (employeeIds == null || employeeIds.isEmpty()) {
      return employees;
    }

    // Build the SQL query with placeholders
    StringBuilder sqlBuilder =
        new StringBuilder("SELECT id, name, email FROM employees WHERE id IN (");
    for (int i = 0; i < employeeIds.size(); i++) {
      if (i > 0) sqlBuilder.append(", ");
      sqlBuilder.append("?");
    }
    sqlBuilder.append(")");

    try (Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

      // Set parameters
      for (int i = 0; i < employeeIds.size(); i++) {
        pstmt.setString(i + 1, employeeIds.get(i));
      }

      // Execute query
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          employees.add(
              new Employee(rs.getString("id"), rs.getString("name"), rs.getString("email")));
        }
      }

      log.info("Retrieved {} employees from database", employees.size());

    } catch (SQLException e) {
      log.error("Failed to retrieve employees from database", e);
      throw new RuntimeException("Database query failed", e);
    }

    return employees;
  }

  public static List<Employee> getAllEmployees() {
    List<Employee> employees = new ArrayList<>();

    String sql = "SELECT id, name, email FROM employees";

    try (Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {

      while (rs.next()) {
        employees.add(
            new Employee(rs.getString("id"), rs.getString("name"), rs.getString("email")));
      }

      log.info("Retrieved {} employees from database", employees.size());

    } catch (SQLException e) {
      log.error("Failed to retrieve all employees from database", e);
      throw new RuntimeException("Database query failed", e);
    }

    return employees;
  }
}
