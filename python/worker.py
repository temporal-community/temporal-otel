import asyncio
from concurrent.futures import ThreadPoolExecutor

from temporalio.client import Client
from temporalio.contrib.opentelemetry import TracingInterceptor
from temporalio.worker import Worker

from python.trace import create_tracer, instrument
from python.workflow import TASK_QUEUE, HttpWorkflow, http_get


async def main():
    tracer = create_tracer()
    instrument()
    client = await Client.connect(
        "localhost:7233",
        interceptors=[TracingInterceptor(tracer=tracer)],
    )

    worker = Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[HttpWorkflow],
        activities=[http_get],
        activity_executor=ThreadPoolExecutor(2),
    )
    await worker.run()


if __name__ == "__main__":
    asyncio.run(main())
