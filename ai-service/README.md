# AI Service

**AI Service** cung cấp các khả năng trí tuệ nhân tạo cho nền tảng E-commerce, bao gồm tìm kiếm vector cho sản phẩm, tạo embedding cho hình ảnh, và các hệ thống gợi ý tiềm năng.

## 🐍 Technology Stack
- **Ngôn ngữ:** Python 3.9+
- **Framework:** FastAPI / Flask (dự đoán)
- **ML Libraries:** PyTorch, Sentence Transformers / CLIP (cho image embeddings)
- **Vector Database:** Sử dụng lưu trữ cục bộ hoặc vector DB bên ngoài

## 🔌 Cấu hình
- **Port:** `8000`
- **Docker Image:** `maianhhoang31072003/ai-service:latest`

## 🚀 Chạy Dịch vụ

### Docker
```bash
docker build -t ai-service .
docker run -p 8000:8000 ai-service
```

### Local Development
1. Cài đặt dependencies:
   ```bash
   pip install -r requirements.txt
   ```
2. Chạy ứng dụng:
   ```bash
   uvicorn main:app --reload --host 0.0.0.0 --port 8000
   ```
