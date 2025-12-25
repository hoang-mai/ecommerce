# Dockerfile for AI-service (FastAPI + uvicorn)
# Base image: Python slim (CPU-only). For GPU/PyTorch CUDA use the official PyTorch CUDA images.

FROM python:3.11-slim-bullseye

# Install system dependencies required by Pillow, confluent-kafka (librdkafka), and possible build tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    git \
    curl \
    ca-certificates \
    libjpeg-dev \
    zlib1g-dev \
    libpng-dev \
    libtiff5-dev \
    librdkafka-dev \
    libsndfile1 \
    libgl1-mesa-glx \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy requirements first to leverage Docker layer caching
COPY requirements.txt /app/

# Upgrade pip and install python deps
RUN pip install --upgrade pip setuptools wheel && \
    pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . /app

# Create a non-root user and set ownership
RUN useradd --create-home appuser && chown -R appuser:appuser /app
USER appuser

# Expose FastAPI default port
EXPOSE 8000

# Default command to run the service
# For development you can append --reload, but avoid in production
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]



