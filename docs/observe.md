# Export Temporal OpenTelemetry data to Observe

This guide helps you integrate Temporal Workflows with Observe for complete observability using OpenTelemetry.

## Setup

1. Follow [Quick Start](../README.md#quick-start) instructions to install your dependencies.

1. Make a copy of [.envrc.example](../.envrc.example).

    ```bash
    cp .envrc.observe .envrc
    ```

1. In Observe, [create an ingest token][1]

    > If you don't have an Observe account, you can [sign up for a free Dynatrace trial account][2].

1. Set your Observe environment variables in `.envrc` for PowerShell or Bash:

    ```powershell
    # Replace with your actual Observe tenant and API token
    $Env:$OBSERVE_ID="123456789012"
    $Env:$OBSERVE_TOKEN="abc:123"
    ```

    ```bash
    # Replace with your actual Observe tenant and API token
    export OBSERVE_ID="123456789012"
    export OBSERVE_TOKEN="abc:123"
    ```

1. Start the Temporal + Observe Stack with Docker Compose or bash:

    ```powershell
    # This starts Temporal server + OpenTelemetry Collector configured for Observe with Docker
    docker compose --env-file .envrc --profile observe up -d
    ```

    ```bash
    # This starts Temporal server + OpenTelemetry Collector configured for Observe
    uv run poe up --profile observe
    ```

    Wait for all services to start (about 30-60 seconds).

1. Run the Demo Workflow in a new terminal:

    ```
    uv run poe client
    ```

    This will:
    * Execute HTTP GET workflows every 2 seconds
    * Generate traces, metrics, and logs
    * Send all telemetry data to your Observe tenant

    If you don't have `uv` you can install it with `pip install uv`.

## 📊 Exploring Your Data in Dynatrace

Now that telemetry is flowing, explore your data in Observe.

* [APM Observability | Trace Explorer ][3]
* [Metrics Explorer | Observe][4]
* [Log Explorer | Observe][5]

---

**Questions or Issues?** Open an issue in this repository.

[1]: https://docs.observeinc.com/docs/datastreams#create-a-token
[2]: https://account.observeinc.com/
[3]: https://docs.observeinc.com/docs/trace-explorer
[4]: https://docs.observeinc.com/docs/metrics-explorer
[5]: https://docs.observeinc.com/docs/log-explorer