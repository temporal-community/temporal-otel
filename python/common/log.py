from opentelemetry.exporter.otlp.proto.grpc._log_exporter import OTLPLogExporter
from opentelemetry.sdk._logs import LoggerProvider, LoggingHandler
from opentelemetry.sdk._logs.export import SimpleLogRecordProcessor
from opentelemetry.sdk.resources import Resource

from python.common.settings import settings


def create_logging_handler() -> LoggingHandler:
    logger_provider = LoggerProvider(
        resource=Resource.create(
            {
                "service.name": "temporal",
            }
        ),
    )

    logger_provider.add_log_record_processor(
        SimpleLogRecordProcessor(
            OTLPLogExporter(
                endpoint=settings.OTLP_ENDPOINT,
                insecure=True,
            )
        )
    )
    return LoggingHandler(logger_provider=logger_provider)
