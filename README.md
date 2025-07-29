# Temporal OpenTelemetry Samples

This project demonstrates how to integrate [Temporal][1] with [OpenTelemetry][2] for complete observability of your workflow executions. It includes:

- 📊 **Complete observability stack**: OpenTelemetry traces, metrics, and logs
- ⚡ **Temporal Workflow**: HTTP GET workflow with full telemetry instrumentation
- 🔄 **OpenTelemetry Collector**: Central hub for processing and routing telemetry data
- 🚀 **Two deployment options**:
  - Open-source tools (Jaeger, Prometheus, and Elasticsearch)
  - Dynatrace

> 💡 **New to OpenTelemetry?** Check out our [OpenTelemetry Primer](docs/opentelemetry-primer.md) to understand the concepts, benefits, and why it matters for Temporal developers.

## Usage

### Prerequisites

- [uv](https://docs.astral.sh/uv/getting-started/installation/)
- [Docker](https://docs.docker.com/engine/install/)

### Quick Start

1. **Install Python and dependencies:**

    ```bash
    # Install Python 3.12 if needed
    uv python install 3.12

    # Install all project dependencies
    uv sync --dev
    ```

1. **Start the observability stack:**

    ```bash
    # Starts Temporal server + Jaeger + Prometheus + Elasticsearch + OTEL Collector
    uv run poe up
    ```

    Wait for all services to start (about 30-60 seconds). You'll see logs from multiple containers.

1. **Run the demo workflow** (in another new terminal):

    ```bash
    uv run poe client
    ```

    This continuously executes HTTP workflows every 2 seconds. You'll see workflow IDs being printed.

### Explore Your Telemetry Data

Now that everything is running, explore the observability data:

#### 🔍 **Distributed Traces** → [Jaeger][3]

- Go to [http://localhost:16686/](http://localhost:16686/)
- Click "Find Traces" to see traces
- Click on any trace to see the complete Temporal Workflow → Activity call chain

#### 📊 **Metrics** → [Prometheus][4]

- Go to [http://localhost:9090/](http://localhost:9090/)
- Try queries like:
  - `temporal_workflow_completed_total` - completed workflows
  - `temporal_worker_task_slots_available` - available execution slots in Worker

#### 📝 **Logs** → [Elasticsearch][5]

- Query logs: `curl http://localhost:9200/temporal-logs/_search | jq`
- Or browse with: `curl http://localhost:9200/_cat/indices`

#### ⚙️ **Temporal UI** → [Temporal Web](http://localhost:8233/)

- Go to [http://localhost:8233/](http://localhost:8233/)
- View running workflows, activity history, and task queues

## 🛠️ Deployment Options

By default, this project uses open-source observability platforms (Jaeger, Prometheus, and Elasticsearch)
to storage Temporal traces, metrics, and logs.

To learn more about how this solution works with third-party observability vendors,
use the following step-by-step instructions:

1. [Dynatrace](docs/dynatrace.md)

> 🏗️ **Want to understand the architecture?** See our [Architecture Documentation](docs/architecture.md) for a comprehensive overview of how Temporal and OpenTelemetry components work together.

## 🤝 Contributing

We welcome contributions from the community!
Whether you want to add new OpenTelemetry integrations, improve documentation,
or enhance existing examples, check out our [Contributing Guide](CONTRIBUTING.md)
to get started.

[1]: https://docs.temporal.io/encyclopedia/temporal-sdks#official-sdks
[2]: https://opentelemetry.io/docs/
[3]: https://www.jaegertracing.io/
[4]: https://prometheus.io/
[5]: https://www.elastic.co/elasticsearch
