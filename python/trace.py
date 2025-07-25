from opentelemetry import trace
from opentelemetry.instrumentation.aiohttp_client import AioHttpClientInstrumentor
from opentelemetry.sdk.resources import Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor, ConsoleSpanExporter
from opentelemetry.semconv.attributes import service_attributes


def create_tracing_pipeline() -> BatchSpanProcessor:
    console_exporter = ConsoleSpanExporter()
    span_processor = BatchSpanProcessor(console_exporter)
    return span_processor


def create_tracer() -> trace.Tracer:
    """Create a tracer."""
    SERVICE_NAME = "temporal"
    rc = Resource.create(
        {
            service_attributes.SERVICE_NAME: SERVICE_NAME,
        }
    )
    processor = create_tracing_pipeline()
    provider = TracerProvider(resource=rc)
    provider.add_span_processor(processor)
    trace.set_tracer_provider(provider)

    return trace.get_tracer(SERVICE_NAME)


def instrument():
    """Instrument third-party libraries."""
    instrumentations = [
        AioHttpClientInstrumentor(),
    ]
    for instrumentation in instrumentations:
        instrumentation.instrument()
