package io.temporal.otel.utils;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.opentracingshim.OpenTracingShim;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.ServiceAttributes;
import io.opentracing.Tracer;
import io.temporal.opentracing.OpenTracingOptions;
import io.temporal.opentracing.OpenTracingSpanContextCodec;

public class TraceUtils {
  private static final String SERVICE_NAME = "temporal-java";

  public static OpenTracingOptions getTraceOptions(String otlpEndpoint) {
    return getTraceOpenTelemetryOptions(otlpEndpoint);
  }

  private static OpenTracingOptions getTraceOpenTelemetryOptions(String otlpEndpoint) {
    Resource resource =
        Resource.getDefault()
            .merge(Resource.builder().put(ServiceAttributes.SERVICE_NAME, SERVICE_NAME).build());

    OtlpGrpcSpanExporter spanExporter =
        OtlpGrpcSpanExporter.builder().setEndpoint(otlpEndpoint).build();

    SdkTracerProvider tracerProvider =
        SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .setResource(resource)
            .setSampler(Sampler.alwaysOn())
            .build();

    OpenTelemetrySdk openTelemetry =
        OpenTelemetrySdk.builder()
            .setPropagators(
                ContextPropagators.create(
                    TextMapPropagator.composite(W3CTraceContextPropagator.getInstance())))
            .setTracerProvider(tracerProvider)
            .build();

    // create OpenTracing shim and return OpenTracing Tracer from it
    return getOpenTracingOptionsForTracer(OpenTracingShim.createTracerShim(openTelemetry));
  }

  private static OpenTracingOptions getOpenTracingOptionsForTracer(Tracer tracer) {
    OpenTracingOptions options =
        OpenTracingOptions.newBuilder()
            .setSpanContextCodec(OpenTracingSpanContextCodec.TEXT_MAP_CODEC)
            .setTracer(tracer)
            .build();
    return options;
  }
}
