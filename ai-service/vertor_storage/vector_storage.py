import json
import numpy as np
from typing import List, Dict, Optional, Union
from datetime import datetime, UTC
import logging
from pathlib import Path

logger = logging.getLogger(__name__)


class VectorStorage:
    """
    Class to handle vector storage using file system
    - Single vectors.npy file for all product vectors
    - Single metadata.json file for all metadata
    """

    def __init__(self, storage_dir: str = "vector_data"):
        """
        Khởi tạo VectorStorage

        Args:
            storage_dir: Thư mục chứa vectors và metadata
        """
        self.storage_dir = Path(storage_dir)
        self.vectors_file = self.storage_dir / "vectors.npy"
        self.metadata_file = self.storage_dir / "metadata.json"

        # Tạo thư mục nếu chưa tồn tại
        self.storage_dir.mkdir(parents=True, exist_ok=True)

        # Load metadata và vectors
        self.metadata = self._load_metadata()
        self.vectors_cache = self._load_all_vectors()

        logger.info(f"✅ VectorStorage initialized at: {self.storage_dir.absolute()}")
        logger.info(f"   📊 Loaded {len(self.metadata)} products")

    def _load_metadata(self) -> Dict:
        """Load metadata từ file JSON"""
        if self.metadata_file.exists():
            try:
                with open(self.metadata_file, 'r', encoding='utf-8') as f:
                    return json.load(f)
            except Exception as e:
                logger.error(f"❌ Error loading metadata: {e}")
                return {}
        return {}

    def _load_all_vectors(self) -> Dict[str, np.ndarray]:
        """
        Load tất cả vectors từ file .npy

        Returns:
            Dictionary {product_id: numpy_array}
        """
        if self.vectors_file.exists():
            try:
                # Load vectors array
                all_vectors = np.load(self.vectors_file, allow_pickle=True).item()
                if isinstance(all_vectors, dict):
                    logger.info(f"✅ Loaded vectors for {len(all_vectors)} products from cache")
                    return all_vectors
            except Exception as e:
                logger.error(f"❌ Error loading vectors: {e}")
        return {}

    def _save_metadata(self):
        """Save metadata ra file JSON"""
        try:
            with open(self.metadata_file, 'w', encoding='utf-8') as f:
                json.dump(self.metadata, f, indent=2, ensure_ascii=False)
            logger.debug(f"✅ Metadata saved to {self.metadata_file}")
        except Exception as e:
            logger.error(f"❌ Error saving metadata: {e}")

    def _save_all_vectors(self):
        """Save tất cả vectors vào 1 file .npy"""
        try:
            np.save(self.vectors_file, self.vectors_cache, allow_pickle=True)
            logger.debug(f"✅ Vectors saved to {self.vectors_file}")
        except Exception as e:
            logger.error(f"❌ Error saving vectors: {e}")

    def save_product_vectors(self, product_id: str, image_urls: List[str], vectors: List[np.ndarray]) -> bool:
        """
        Lưu vectors của ảnh sản phẩm vào storage

        Args:
            product_id: ID của sản phẩm
            image_urls: Danh sách URLs của ảnh
            vectors: Danh sách vectors tương ứng với mỗi ảnh

        Returns:
            True nếu thành công, False nếu thất bại
        """
        try:
            # Filter out None vectors
            valid_data = [(url, vec) for url, vec in zip(image_urls, vectors) if vec is not None]

            if not valid_data:
                logger.warning(f"⚠️ No valid vectors to save for product {product_id}")
                return False

            valid_urls, valid_vectors = zip(*valid_data)

            # Convert to numpy array
            vectors_array = np.array(valid_vectors)

            # Update vectors cache
            self.vectors_cache[product_id] = vectors_array

            # Update metadata
            self.metadata[product_id] = {
                'product_id': product_id,
                'image_urls': list(valid_urls),
                'num_images': len(valid_vectors),
                'vector_shape': list(vectors_array.shape),
                'updated_at': datetime.now(UTC).isoformat()
            }

            # Save to disk
            self._save_all_vectors()
            self._save_metadata()

            logger.info(f"✅ Saved {len(valid_vectors)} vectors for product {product_id}")
            logger.info(f"   📊 Shape: {vectors_array.shape}")

            return True

        except Exception as e:
            logger.error(f"❌ Error saving vectors for product {product_id}: {e}", exc_info=True)
            return False

    def get_product_vectors(self, product_id: str) -> Optional[np.ndarray]:
        """
        Lấy vectors của sản phẩm từ cache

        Args:
            product_id: ID của sản phẩm

        Returns:
            numpy array chứa vectors hoặc None nếu không tìm thấy
        """
        try:
            if product_id not in self.vectors_cache:
                logger.warning(f"⚠️ Product {product_id} not found in vectors cache")
                return None

            vectors = self.vectors_cache[product_id]
            logger.info(f"✅ Loaded {vectors.shape[0]} vectors for product {product_id}")
            return vectors

        except Exception as e:
            logger.error(f"❌ Error loading vectors for product {product_id}: {e}")
            return None

    def get_product_metadata(self, product_id: str) -> Optional[Dict]:
        """
        Lấy metadata của sản phẩm

        Args:
            product_id: ID của sản phẩm

        Returns:
            Dictionary chứa metadata hoặc None nếu không tìm thấy
        """
        return self.metadata.get(product_id)

    def get_all_product_vectors(self) -> Dict[str, np.ndarray]:
        """
        Lấy tất cả vectors của các sản phẩm

        Returns:
            Dictionary {product_id: numpy_array}
        """
        logger.info(f"✅ Returning vectors for {len(self.vectors_cache)} products")
        return self.vectors_cache.copy()

    def get_all_metadata(self) -> Dict:
        """
        Lấy tất cả metadata

        Returns:
            Dictionary chứa metadata của tất cả sản phẩm
        """
        return self.metadata.copy()

    def delete_product_vectors(self, product_id: str) -> bool:
        """
        Xóa vectors của sản phẩm

        Args:
            product_id: ID của sản phẩm

        Returns:
            True nếu thành công, False nếu thất bại
        """
        try:
            if product_id not in self.metadata:
                logger.warning(f"⚠️ Product {product_id} not found")
                return False

            # Remove from cache
            if product_id in self.vectors_cache:
                del self.vectors_cache[product_id]

            # Remove from metadata
            del self.metadata[product_id]

            # Save updated data
            self._save_all_vectors()
            self._save_metadata()

            logger.info(f"✅ Deleted vectors for product {product_id}")
            return True

        except Exception as e:
            logger.error(f"❌ Error deleting vectors for product {product_id}: {e}")
            return False

    def list_products(self) -> List[str]:
        """
        Liệt kê tất cả product IDs

        Returns:
            List các product IDs
        """
        return list(self.metadata.keys())

    def get_storage_stats(self) -> Dict[str, Union[int, float, str]]:
        """
        Lấy thống kê về storage

        Returns:
            Dictionary chứa thống kê
        """
        stats: Dict[str, Union[int, float, str]] = {
            'total_products': len(self.metadata),
            'total_images': sum(meta['num_images'] for meta in self.metadata.values()),
            'storage_dir': str(self.storage_dir.absolute()),
            'metadata_file': str(self.metadata_file.absolute()),
            'vectors_file': str(self.vectors_file.absolute())
        }

        # Calculate file sizes
        vectors_size = self.vectors_file.stat().st_size if self.vectors_file.exists() else 0
        metadata_size = self.metadata_file.stat().st_size if self.metadata_file.exists() else 0

        stats['vectors_size_mb'] = float(round(vectors_size / (1024 * 1024), 2))
        stats['metadata_size_kb'] = float(round(metadata_size / 1024, 2))
        stats['total_size_mb'] = float(round((vectors_size + metadata_size) / (1024 * 1024), 2))

        return stats

