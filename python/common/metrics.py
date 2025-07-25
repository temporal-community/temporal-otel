from temporalio.runtime import OpenTelemetryConfig, Runtime, TelemetryConfig

from python.common.settings import settings


def create_runtime() -> Runtime:
    return Runtime(
        telemetry=TelemetryConfig(
            metrics=OpenTelemetryConfig(
                url=settings.OTLP_ENDPOINT,
                http=False,  # use gRPC
            )
        )
    )
