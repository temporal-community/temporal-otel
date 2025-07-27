"""
Basic example of using Temporal with OpenTelemetry.
"""

from datetime import timedelta

import aiohttp
from temporalio import activity, workflow


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
