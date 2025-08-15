from opentelemetry import trace
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.instrumentation.aiohttp_client import AioHttpClientInstrumentor
from opentelemetry.sdk.resources import Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor, SpanProcessor
from opentelemetry.semconv.attributes import service_attributes

from python.common.settings import settings


def create_processors() -> list[SpanProcessor]:
    return [
        BatchSpanProcessor(
            OTLPSpanExporter(
                endpoint=settings.OTLP_ENDPOINT,
                insecure=True,
            )
        ),
    ]


def create_tracer() -> trace.Tracer:
    """Create a tracer."""
    SERVICE_NAME = "temporal-python"
    # Create resource attributes and create a tracer provider
    rc = Resource.create(
        {
            service_attributes.SERVICE_NAME: SERVICE_NAME,
        }
    )
    provider = TracerProvider(resource=rc)

    # Create processors and add them to the tracer provider
    processors = create_processors()
    for processor in processors:
        provider.add_span_processor(processor)

    # Set the tracer provider
    trace.set_tracer_provider(provider)

    return trace.get_tracer(SERVICE_NAME)


def instrument():
    """Instrument third-party libraries."""
    instrumentations = [
        AioHttpClientInstrumentor(),
    ]
    for instrumentation in instrumentations:
        instrumentation.instrument()
