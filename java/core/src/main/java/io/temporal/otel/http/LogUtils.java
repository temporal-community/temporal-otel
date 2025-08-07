package io.temporal.otel.http;

import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.ServiceAttributes;

public class LogUtils {
  private static final String SERVICE_NAME = "temporal-java";
  private static OpenTelemetrySdk openTelemetry;

  public static OpenTelemetrySdk getOpenTelemetry() {
    if (openTelemetry == null) {
      openTelemetry = buildOpenTelemetry();
    }
    return openTelemetry;
  }

  private static OpenTelemetrySdk buildOpenTelemetry() {
    Resource resource =
        Resource.getDefault()
            .merge(Resource.builder().put(ServiceAttributes.SERVICE_NAME, SERVICE_NAME).build());

    OtlpGrpcLogRecordExporter logExporter =
        OtlpGrpcLogRecordExporter.builder().setEndpoint("http://localhost:4317").build();

    SdkLoggerProvider loggerProvider =
        SdkLoggerProvider.builder()
            .setResource(resource)
            .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
            .build();

    return OpenTelemetrySdk.builder().setLoggerProvider(loggerProvider).build();
  }
}
