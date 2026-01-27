import json
import logging
from confluent_kafka import Consumer, KafkaException, KafkaError
from typing import Callable, Optional, Dict, List, Union
import threading
import os
from dotenv import load_dotenv
from image_process.image_process import encode_multiple_images
from vertor_storage.vector_storage import VectorStorage

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class ProductEventConsumer:

    def __init__(self,
                 bootstrap_servers: str = None,
                 group_id: str = "ai-service-group",
                 topics: Union[str, List[str]] = "ai-service-topic.upload-product-image",
                 auto_offset_reset: str = "earliest",
                 vector_storage: VectorStorage = None
                 ):
        """
        Khởi tạo ProductEventConsumer

        Args:
            bootstrap_servers: Địa chỉ Kafka broker
            group_id: Consumer group ID
            topics: Topic hoặc danh sách topics để subscribe
            auto_offset_reset: Chiến lược đọc offset (earliest/latest)
        """
        self.bootstrap_servers = bootstrap_servers or os.getenv('KAFKA_BOOTSTRAP_SERVERS', 'localhost:19092')
        self.group_id = group_id

        # Hỗ trợ cả string và list
        if isinstance(topics, str):
            self.topics = [topics]
        else:
            self.topics = topics

        # Giữ lại topic (singular) để backward compatibility
        self.topic = self.topics[0] if self.topics else "product-events"

        self.auto_offset_reset = auto_offset_reset

        # Cấu hình consumer
        self.config = {
            'bootstrap.servers': self.bootstrap_servers,
            'group.id': self.group_id,
            'auto.offset.reset': self.auto_offset_reset,
            'allow.auto.create.topics': True,
            'enable.auto.commit': True,
            'auto.commit.interval.ms': 5000,
            'session.timeout.ms': 6000,
            'max.poll.interval.ms': 300000
        }

        self.consumer: Optional[Consumer] = None
        self.running = False
        self.thread: Optional[threading.Thread] = None
        self.topic_handlers: Dict[str, Callable] = {
            "ai-service-topic.upload-product-image": self.handle_upload_images_product,
        }

        self.vector_storage = vector_storage

        logger.info(f"ProductEventConsumer initialized with topics: {', '.join(self.topics)}")

    def start(self):
        """
        Bắt đầu consumer trong background thread
        """
        if self.running:
            logger.warning("Consumer is already running")
            return

        self.running = True
        self.thread = threading.Thread(target=self._consume_loop, daemon=True)
        self.thread.start()
        logger.info(f"Consumer started on topics: {', '.join(self.topics)}")

    def stop(self):
        """
        Dừng consumer
        """
        self.running = False
        if self.consumer:
            self.consumer.close()
            logger.info("Consumer stopped and closed")
        if self.thread:
            self.thread.join(timeout=5)
        logger.info("✅ Consumer shutdown complete")


    def _consume_loop(self):
        """
        Vòng lặp chính để consume messages từ Kafka
        """
        try:
            self.consumer = Consumer(self.config)
            self.consumer.subscribe(self.topics)
            logger.info(f"Subscribed to topics: {', '.join(self.topics)}")

            while self.running:
                msg = self.consumer.poll(timeout=1.0)

                if msg is None:
                    continue

                if msg.error():
                    if msg.error().code() == KafkaError._PARTITION_EOF:
                        # End of partition
                        logger.debug(f"Reached end of partition {msg.partition()}")
                    elif msg.error().code() == KafkaError.UNKNOWN_TOPIC_OR_PART:
                        # Topic doesn't exist yet - log warning but don't crash
                        logger.warning(f"⚠️ Topic not available: {msg.error().str()}")
                        logger.warning(f"   Waiting for topic to be created...")
                    else:
                        raise KafkaException(msg.error())
                else:
                    # Xử lý message
                    topic = msg.topic()
                    handler = self.topic_handlers.get(topic)
                    if handler:
                        handler(msg)
                    else:
                        logger.warning(f"⚠️ No handler registered for topic: {topic}")

        except Exception as e:
            logger.error(f"Error in consume loop: {e}", exc_info=True)
        finally:
            if self.consumer:
                self.consumer.close()

    def handle_upload_images_product(self, msg):
        """
        Xử lý sự kiện upload-images-product

        Flow:
        1. Nhận event từ Kafka với productId và imageUrls
        2. Tải và encode từng ảnh thành vector sử dụng CLIP model
        3. Lưu vectors vào file .npy và metadata.json
        """
        try:
            event = json.loads(msg.value().decode('utf-8'))
            product_id = event.get('productId')
            image_urls = event.get('imageUrls', [])

            logger.info(f"📥 Received upload-images-product event for productId: {product_id}")
            logger.info(f"   Images to process: {len(image_urls)}")

            if not product_id or not image_urls:
                logger.warning(f"⚠️ Missing productId or imageUrls in event")
                return

            # Process images and create vectors
            logger.info(f"🔄 Processing {len(image_urls)} images...")
            vectors = encode_multiple_images(image_urls)

            # Count successful encodings
            successful = sum(1 for v in vectors if v is not None)
            failed = len(vectors) - successful

            logger.info(f"   ✅ Successfully encoded: {successful}/{len(image_urls)}")
            if failed > 0:
                logger.warning(f"   ⚠️ Failed to encode: {failed}/{len(image_urls)}")

            # Save vectors to file storage (.npy + metadata.json)
            if successful > 0:
                logger.info(f"💾 Saving vectors to file storage...")
                success = self.vector_storage.save_product_vectors(product_id, image_urls, vectors)
                if success:
                    logger.info(f"✅ Successfully saved vectors for product {product_id}")
                    # Log storage stats
                    stats = self.vector_storage.get_storage_stats()
                    logger.info(f"📊 Storage stats: {stats['total_products']} products, "
                              f"{stats['total_images']} images, {stats['total_size_mb']} MB")
                else:
                    logger.error(f"❌ Failed to save vectors for product {product_id}")
            else:
                logger.error(f"❌ No valid vectors to save for product {product_id}")

        except json.JSONDecodeError as e:
            logger.error(f"❌ Invalid JSON in message: {e}")
        except Exception as e:
            logger.error(f"❌ Error processing upload-images-product event: {e}", exc_info=True)
