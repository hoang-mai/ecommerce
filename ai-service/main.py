from fastapi import FastAPI, UploadFile, File
from contextlib import asynccontextmanager
import logging
import os
from dotenv import load_dotenv
from prometheus_fastapi_instrumentator import Instrumentator
from messaging.consumer.product_event_consumer import ProductEventConsumer
from service.search_images import ImageSearchService
from vertor_storage.vector_storage import VectorStorage

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Global instances
consumer: ProductEventConsumer = None
image_search_service: ImageSearchService = None
vector_storage: VectorStorage = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Quản lý lifecycle của ứng dụng
    """
    # Startup
    logger.info("🚀 Starting AI Service...")

    global consumer, image_search_service

    try:
        # Khởi tạo ImageSearchService
        logger.info("📦 Initializing Image Search Service...")
        vector_storage_dir = os.getenv('VECTOR_STORAGE_DIR', 'vector_data')
        vector_storage = VectorStorage(storage_dir=vector_storage_dir)
        image_search_service = ImageSearchService(vector_storage=vector_storage)

        kafka_servers = os.getenv('KAFKA_BOOTSTRAP_SERVERS', 'kafka:9092')
        kafka_group = os.getenv('KAFKA_GROUP_ID', 'ai-service-group')
        kafka_topics_str = os.getenv('KAFKA_TOPICS', 'ai-service-topic.upload-product-image')
        kafka_topics = [topic.strip() for topic in kafka_topics_str.split(',')]

        logger.info(f"📋 Subscribing to topics: {', '.join(kafka_topics)}")

        consumer = ProductEventConsumer(
            bootstrap_servers=kafka_servers,
            group_id=kafka_group,
            topics=kafka_topics,
            vector_storage=vector_storage
        )

        # Start consumer
        consumer.start()
        logger.info("✅ Kafka Consumer started successfully")

    except Exception as e:
        logger.error(f"❌ Error during startup: {e}", exc_info=True)

    yield

    # Shutdown
    logger.info("🛑 Shutting down AI Service...")

    if consumer:
        consumer.stop()
        logger.info("✅ Kafka Consumer stopped")


app = FastAPI(
    title="AI Service - E-commerce",
    description="AI Service với Kafka Consumer để xử lý product events và recommendation system",
    version="1.0.0",
    lifespan=lifespan
)

# Setup Prometheus metrics
Instrumentator().instrument(app).expose(app, endpoint="/actuator/prometheus")


@app.get("/")
async def root():
    """
    Health check endpoint
    """
    return {
        "message": "AI Service is running",
        "status": "healthy",
        "consumer_running": consumer.running if consumer else False
    }


@app.get("/hello/{name}")
async def say_hello(name: str):
    """
    Simple hello endpoint
    """
    return {"message": f"Hello {name}"}


@app.post("/search-images")
async def search_images(file: UploadFile = File(...), top_k: int = 5):
    """
    Tìm kiếm ảnh tương tự từ ảnh upload

    Args:
        file: Ảnh upload
        top_k: Số lượng kết quả trả về (mặc định 5)

    Returns:
        JSON chứa danh sách sản phẩm tương tự nhất
    """
    if not image_search_service:
        return {
            "success": False,
            "error": "Image Search Service not initialized",
            "results": []
        }

    # Validate file type
    if not file.content_type.startswith('image/'):
        return {
            "success": False,
            "error": f"Invalid file type: {file.content_type}. Please upload an image file.",
            "results": []
        }

    logger.info(f"🔍 Received search request for image: {file.filename} (type: {file.content_type})")

    # Gọi service để xử lý tìm kiếm
    result = await image_search_service.search_by_uploaded_image(file, top_k)

    return result
