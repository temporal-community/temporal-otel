# Contributing to Temporal OpenTelemetry Samples

Thank you for your interest in contributing to this project! This guide outlines how the open source community can help improve Temporal OpenTelemetry integration examples.

## 🤝 Types of Contributions

We welcome all types of contributions:

- **Code contributions**: Bug fixes, new features, performance improvements
- **Documentation**: Improve existing docs, add new guides, fix typos
- **Bug reports**: Report issues with clear reproduction steps
- **Feature requests**: Suggest new OpenTelemetry integrations or observability examples
- **Testing**: Add test cases, improve test coverage

## 🚀 Development Environment Setup

Follow the setup instructions in the [README.md](README.md#quick-start) to get started.

1. **Verify setup**:

   ```bash
   # Start the full stack
   uv run poe up

   # Run linting/formatting checks
   uv run poe lint
   uv run poe format
   ```

## 📋 Contribution Process

1. **Fork** the repository on GitHub
1. **Clone** your fork locally:

   ```bash
   git clone https://github.com/YOUR_USERNAME/temporal-otel.git
   cd temporal-otel
   ```

1. **Create a branch** for your changes:

   ```bash
   git checkout -b feature/your-feature-name
   ```

1. **Make your changes** and ensure they follow our coding standards
1. **Test your changes** locally
1. **Commit** with clear, descriptive messages
1. **Push** to your fork and create a **Pull Request**
1. A **maintainer** of this repository will review your **Pull Request**
1. **Respond to code review** feedback promptly

## 🎨 Coding Style and Formatting

This project uses [Ruff](https://docs.astral.sh/ruff/) for code formatting and linting.
Its configuration is in [`pyproject.toml`](pyproject.toml).

**Before submitting**:

```bash
# Auto-format code
uv run poe format

# Check for linting issues
uv run poe lint
```

All Pull Requests must pass linting checks.

## 💬 Communication

Open a GitHub Issue if you have questions, report a bug, or request for enhancements.

## 📊 Contribution Opportunities

| Area | Description | Difficulty | Skills Needed |
|------|-------------|------------|---------------|
| **OpenTelemetry Backends** | Integrate with more observability platforms | Intermediate | OpenTelemetry |
| **Multi-language** | Add Java / Go / TypeScript / .NET / Ruby / PHP examples | Advanced | Temporal |
| **Testing** | Add integration tests | Intermediate | `pytest` |
| **CI/CD** | Improve GitHub Actions workflows | Intermediate | GitHub Actions, Docker |
| **Documentation** | Improve setup guides, add troubleshooting tips | Beginner | Technical Writing |

