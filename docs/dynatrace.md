# Export Temporal OpenTelemetry data to Dynatrace

This guide helps you integrate Temporal Workflows with Dynatrace for complete observability using OpenTelemetry.

> Credits to [Let’s learn how to send OpenTelemetry data to Dynatrace together!][1] by [Adriana Villela][2].

## Setup

1. Follow [Quick Start](../README.md#quick-start) instructions to install your dependencies.

1. Make a copy of [.envrc.example](../.envrc.example).

    ```bash
    cp .envrc.example .envrc
    ```

1. In Dynatrace, [create an access token][3] with the following permissions:
   * *Ingest logs (ingest)*
   * *Ingest metrics (ingest)*
   * *Ingest OpenTelemetry traces (ingest)*

    > If you don't have a Dynatrace account, you can [sign up for a free Dynatrace trial account][4].

1. Set your Dynatrace environment variables in `.envrc`:

    ```bash
    # Replace with your actual Dynatrace tenant and API token
    export DYNATRACE_TENANT="abc12345" # from abc12345.live.dynatrace.com
    export DYNATRACE_API_TOKEN="dt0c01.ABC123..."
    ```

1. Start the Temporal + Dynatrace Stack

    ```bash
    # This starts Temporal server + OpenTelemetry Collector configured for Dynatrace
    uv run poe up dynatrace
    ```

    Wait for all services to start (about 30-60 seconds).

1. Run the Demo Workflow in a new terminal:

    ```bash
    uv run poe client
    ```

    This will:
    * Execute HTTP GET workflows every 2 seconds
    * Generate traces, metrics, and logs
    * Send all telemetry data to your Dynatrace tenant

## 📊 Exploring Your Data in Dynatrace

Now that telemetry is flowing, explore your data in Dynatrace:

* [Get started with Distributed Traces | Dynatrace][5]
* [Explore data | Dynatrace][6]
* [Logs | Dynatrace][7]

---

**Questions or Issues?** Open an issue in this repository or consult the Dynatrace community for OpenTelemetry integration support.

[1]: https://www.dynatrace.com/news/blog/send-opentelemetry-data-to-dynatrace/
[2]: https://www.linkedin.com/in/adrianavillela/
[3]: https://docs.dynatrace.com/docs/manage/identity-access-management/access-tokens-and-oauth-clients/access-tokens#create-api-token
[4]: https://www.dynatrace.com/signup/
[5]: https://docs.dynatrace.com/docs/analyze-explore-automate/distributed-traces/analysis/get-started
[6]: https://docs.dynatrace.com/docs/analyze-explore-automate/dashboards-and-notebooks/explore-data
[7]: https://docs.dynatrace.com/docs/analyze-explore-automate/logs/lma-analysis/logs-app