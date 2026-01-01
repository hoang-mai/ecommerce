import requests
from PIL import Image
from io import BytesIO
import torch
import clip
import logging
from typing import List, Optional
import numpy as np
import os
logger = logging.getLogger(__name__)

# Global model instance - will be loaded lazily
_model = None
_preprocess = None

def load_model():
    """Lazy load CLIP model"""
    global _model, _preprocess
    if _model is None:
        logger.info("Loading CLIP model ViT-B/16...")
        _model, _preprocess = clip.load("ViT-B/16", device="cpu", download_root=os.getenv('CLIP_MODEL_DIR', './models'))
        logger.info("✅ CLIP model loaded successfully")
    return _model, _preprocess

def load_image_from_url(url: str) -> Image.Image:
    """
    Tải ảnh từ URL

    Args:
        url: URL của ảnh

    Returns:
        PIL Image object

    Raises:
        requests.RequestException: Nếu không tải được ảnh
        PIL.UnidentifiedImageError: Nếu file không phải ảnh hợp lệ
    """
    resp = requests.get(url, timeout=10)
    resp.raise_for_status()

    return Image.open(BytesIO(resp.content)).convert("RGB")

def encode_image(image: Image.Image) -> np.ndarray:
    """
    Encode ảnh thành vector sử dụng CLIP model

    Args:
        image: PIL Image object

    Returns:
        Normalized vector (numpy array)
    """
    model, preprocess = load_model()

    image_input = preprocess(image).unsqueeze(0)

    with torch.no_grad():
        vector = model.encode_image(image_input)

    # Normalize vector
    vector = vector / vector.norm(dim=-1, keepdim=True)
    return vector.squeeze().numpy()

def encode_image_from_url(url: str) -> Optional[np.ndarray]:
    """
    Tải ảnh từ URL và encode thành vector

    Args:
        url: URL của ảnh

    Returns:
        Vector của ảnh (numpy array) hoặc None nếu lỗi
    """
    try:
        image = load_image_from_url(url)
        logger.info(image)
        vector = encode_image(image)
        logger.info(f"✅ Successfully encoded image from {url}")
        return vector
    except Exception as e:
        logger.error(f"❌ Error encoding image from {url}: {e}")
        return None

def encode_multiple_images(urls: List[str]) -> List[Optional[np.ndarray]]:
    """
    Encode nhiều ảnh từ danh sách URLs

    Args:
        urls: Danh sách URLs của ảnh

    Returns:
        List các vectors (có thể chứa None nếu ảnh nào đó lỗi)
    """
    vectors = []
    for url in urls:
        vector = encode_image_from_url(url)
        vectors.append(vector)
    return vectors

