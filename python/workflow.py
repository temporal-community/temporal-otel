"""
Basic example of using structlog with Temporal.
"""

import asyncio
import uuid
from datetime import timedelta

import aiohttp
from temporalio import activity, workflow
from temporalio.client import Client

TASK_QUEUE = "otel-task-queue"


@activity.defn
async def http_get(url: str) -> str:
    """
    A basic activity that makes an HTTP GET call.
    """
    activity.logger.info("Activity: making HTTP GET call to %s", url)
    async with aiohttp.ClientSession() as session:
        async with session.get(url) as response:
            return await response.text()


@workflow.defn
class HttpWorkflow:
    """
    A basic workflow that makes an HTTP GET call.
    """

    @workflow.run
    async def run(self, url: str) -> str:
        """
        Run the workflow.
        """
        workflow.logger.info("Workflow: triggering HTTP GET activity to %s", url)
        return await workflow.execute_activity(
            http_get,
            url,
            start_to_close_timeout=timedelta(seconds=3),
        )


async def main():
    client = await Client.connect("localhost:7233")
    for _ in range(100):
        workflow_id = f"http-workflow-{uuid.uuid4()}"
        print(f"Executing workflow: {workflow_id}")
        await client.execute_workflow(
            HttpWorkflow.run,
            "https://httpbin.org/get",
            id=workflow_id,
            task_queue=TASK_QUEUE,
        )
        await asyncio.sleep(1)


if __name__ == "__main__":
    asyncio.run(main())
