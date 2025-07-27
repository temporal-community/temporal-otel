import asyncio
import uuid

from temporalio.client import Client
from temporalio.contrib.opentelemetry import TracingInterceptor

from python.common.settings import settings
from python.common.trace import create_tracer
from python.workflow import HttpWorkflow


async def main():
    tracer = create_tracer()
    client = await Client.connect(
        settings.TEMPORAL_HOST,
        interceptors=[TracingInterceptor(tracer=tracer)],
    )

    while True:
        workflow_id = f"http-workflow-{uuid.uuid4()}"
        print(f"Executing workflow: {workflow_id}")
        await client.execute_workflow(
            HttpWorkflow.run,
            "https://httpbin.org/get",
            id=workflow_id,
            task_queue=settings.TEMPORAL_TASK_QUEUE,
        )
        await asyncio.sleep(2)


if __name__ == "__main__":
    asyncio.run(main())
