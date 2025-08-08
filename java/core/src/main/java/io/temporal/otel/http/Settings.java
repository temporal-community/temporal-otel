package io.temporal.otel.http;

public class Settings {
  private static final Settings INSTANCE = new Settings();

  private final String temporalHost;
  private final String otlpEndpoint;

  private Settings() {
    this.temporalHost = getEnvOrDefault("TEMPORAL_HOST", "localhost:7233");
    this.otlpEndpoint = getEnvOrDefault("OTLP_ENDPOINT", "http://localhost:4317");
    validateConfiguration();
  }

  public static Settings getInstance() {
    return INSTANCE;
  }

  private void validateConfiguration() {
    if (temporalHost == null || temporalHost.trim().isEmpty()) {
      throw new IllegalStateException("TEMPORAL_HOST cannot be empty");
    }
    if (otlpEndpoint == null || otlpEndpoint.trim().isEmpty()) {
      throw new IllegalStateException("OTLP_ENDPOINT cannot be empty");
    }
  }

  private String getEnvOrDefault(String key, String defaultValue) {
    String value = System.getenv(key);
    return value != null ? value : defaultValue;
  }

  public String getTemporalHost() {
    return temporalHost;
  }

  public String getOtlpEndpoint() {
    return otlpEndpoint;
  }
}
