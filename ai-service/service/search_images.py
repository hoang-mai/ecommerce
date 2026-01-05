import logging
import numpy as np
from typing import Any
from PIL import Image
from io import BytesIO
from fastapi import UploadFile

from image_process.image_process import encode_image
from vertor_storage.vector_storage import VectorStorage

logger = logging.getLogger(__name__)


class ImageSearchService:
    """
    Service để xử lý tìm kiếm ảnh tương tự
    """

    def __init__(self, vector_storage: VectorStorage):
        """
        Khởi tạo ImageSearchService
        """
        self.vector_storage = vector_storage


    def calculate_similarity(self, vector1: np.ndarray, vector2: np.ndarray) -> float:
        """
        Tính độ tương đồng cosine giữa 2 vectors

        Args:
            vector1: Vector thứ nhất
            vector2: Vector thứ hai

        Returns:
            Cosine similarity score (0-1, càng cao càng giống)
        """
        # Normalize vectors if not already normalized
        v1 = vector1 / np.linalg.norm(vector1)
        v2 = vector2 / np.linalg.norm(vector2)

        # Cosine similarity
        similarity = np.dot(v1, v2)
        logger.info(f"   🔹 Calculated similarity: {similarity:.4f}")
        return float(similarity)

    def find_similar_products(
        self,
        query_vector: np.ndarray,
        top_k: int = 5
    ) -> list[Any] | list[str]:
        """
        Tìm top K sản phẩm có ảnh tương tự nhất

        Args:
            query_vector: Vector của ảnh cần tìm
            top_k: Số lượng kết quả trả về

        Returns:
            Danh sách sản phẩm tương tự với score
        """
        try:
            all_products = self.vector_storage.get_all_product_vectors()
            all_metadata = self.vector_storage.get_all_metadata()
            if not all_products:
                logger.warning("⚠️ No products found in vector storage")
                return []

            # Tính similarity cho từng sản phẩm
            similarities = []

            for product_id, product_vectors in all_products.items():
                metadata = all_metadata.get(str(product_id))
                if metadata is None:
                    logger.warning(f"⚠️ No metadata found for product {product_id}, skipping...")


                # Tính similarity với tất cả ảnh của sản phẩm
                # Lấy similarity cao nhất
                max_similarity = 0.0

                for i, product_vector in enumerate(product_vectors):
                    similarity = self.calculate_similarity(query_vector, product_vector)
                    if similarity > max_similarity:
                        max_similarity = similarity
                if(max_similarity >0.5):
                    similarities.append({
                        'productId': product_id,
                        'similarityScore': max_similarity,
                    })

            # Sort by similarity (cao nhất trước)
            similarities.sort(key=lambda x: x['similarityScore'], reverse=True)

            # Lấy top K
            top_results = similarities[:top_k]

            logger.info(f"✅ Found {len(top_results)} similar products")
            for i, result in enumerate(top_results[:3], 1):
                logger.info(f"   {i}. Product {result['productId']}: {result['similarityScore']:.4f}")

            return top_results

        except Exception as e:
            logger.error(f"❌ Error finding similar products: {e}", exc_info=True)
            return []

    async def search_by_uploaded_image(
        self,
        file: UploadFile,
        top_k: int = 5
    ) -> dict[str, bool | str | None | int | list[Any]] | list[dict]:
        """
        Tìm kiếm sản phẩm tương tự từ ảnh upload

        Args:
            file: File ảnh upload từ user
            top_k: Số lượng kết quả trả về

        Returns:
            Dictionary chứa kết quả tìm kiếm
        """
        try:
            # Đọc file ảnh
            logger.info(f"📤 Processing uploaded image: {file.filename}")
            contents = await file.read()

            # Convert to PIL Image
            image = Image.open(BytesIO(contents)).convert("RGB")
            logger.info(f"✅ Image loaded: {image.size}")

            # Encode ảnh thành vector
            logger.info("🔄 Encoding image...")
            query_vector = encode_image(image)
            logger.info(f"✅ Image encoded to vector: {query_vector.shape}")

            # Tìm sản phẩm tương tự
            logger.info(f"🔍 Searching for top {top_k} similar products...")
            similar_products = self.find_similar_products(query_vector, top_k)


            logger.info(f"✅ Search completed: {len(similar_products)} results found")
            return similar_products

        except Exception as e:
            logger.error(f"❌ Error searching by uploaded image: {e}", exc_info=True)
            return {
                'success': False,
                'error': str(e),
                'query_image': file.filename if file else None,
                'total_results': 0,
                'results': []
            }
