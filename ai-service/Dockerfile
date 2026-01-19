# Multi-stage Dockerfile for AI-service (FastAPI + uvicorn)
# Optimized for smaller image size and faster builds

# ============================================
# Stage 1: Builder - Install dependencies
# ============================================
FROM python:3.11-slim-bullseye AS builder

# Install only build-time dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    git \
    libjpeg-dev \
    zlib1g-dev \
    libpng-dev \
    librdkafka-dev \
 && rm -rf /var/lib/apt/lists/*

# Create virtual environment
RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Copy and install requirements
COPY requirements.txt /tmp/
RUN pip install --upgrade pip setuptools wheel && \
    pip install --no-cache-dir -r /tmp/requirements.txt --extra-index-url https://download.pytorch.org/whl/cpu

# ============================================
# Stage 2: Runtime - Minimal final image
# ============================================
FROM python:3.11-slim-bullseye

# Install only runtime dependencies (no build tools)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libjpeg62-turbo \
    libpng16-16 \
    librdkafka1 \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/* \
 && apt-get clean

WORKDIR /app

# Copy virtual environment from builder
COPY --from=builder /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

COPY . /app
RUN mkdir -p /app/vector_data /models

# Expose FastAPI default port
EXPOSE 8000

# Default command to run the service
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]



