"""Settings for the Temporal AI Samples."""

from pydantic import Field
from pydantic_settings import BaseSettings


class ApplicationSettings(BaseSettings):
    """Settings for the Temporal AI Samples."""

    TEMPORAL_HOST: str = Field(
        description="Temporal server host.",
        default="localhost:7233",
    )
    TEMPORAL_TASK_QUEUE: str = Field(
        description="Temporal task queue for RAG workflows.",
        default="otel-task-queue",
    )


settings = ApplicationSettings()
