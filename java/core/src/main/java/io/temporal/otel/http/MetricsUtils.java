package io.temporal.otel.http;

import io.micrometer.core.instrument.Clock;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import io.temporal.otel.utils.Settings;
import java.util.Map;

public class MetricsUtils {
  private static final String SERVICE_NAME = "temporal-java";

  public static OtlpMeterRegistry getMeterRegistry() {
    Settings settings = Settings.getInstance();
    OtlpConfig otlpConfig =
        new OtlpConfig() {
          @Override
          public String get(String key) {
            return Map.of(
                    "url",
                    settings.getOtlpEndpoint(),
                    "step",
                    "30s",
                    "resourceAttributes.service.name",
                    SERVICE_NAME)
                .get(key);
          }
        };

    return new OtlpMeterRegistry(otlpConfig, Clock.SYSTEM);
  }
}
