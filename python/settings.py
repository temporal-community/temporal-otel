"""Settings for the Temporal AI Samples."""

from pydantic import Field
from pydantic_settings import BaseSettings


class TemporalSettings(BaseSettings):
    """Settings for the Temporal AI Samples."""

    TEMPORAL_HOST: str = Field(
        description="Temporal server host.",
        default="localhost:7233",
    )

    TEMPORAL_TASK_QUEUE: str = Field(
        description="Temporal task queue for RAG workflows.",
        default="otel-task-queue",
    )


class OpenTelemetrySettings(BaseSettings):
    """Settings for the OpenTelemetry."""

    OTLP_TRACE_ENDPOINT: str = Field(
        description="OpenTelemetry endpoint to export traces.",
        default="http://localhost:4318",
    )


class ApplicationSettings(TemporalSettings, OpenTelemetrySettings):
    """Settings for the Temporal AI Samples."""


settings = ApplicationSettings()
