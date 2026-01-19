# Review Service

**Review Service** quản lý các đánh giá và xếp hạng sản phẩm do người dùng gửi.

## ⭐ Tính năng Chính
- **Gửi Đánh giá:** Người dùng có thể xếp hạng và viết đánh giá cho sản phẩm đã mua.
- **Lấy Đánh giá:** Lấy danh sách đánh giá cho một sản phẩm cụ thể.
- **Hỗ trợ Đa phương tiện:** Tải lên hình ảnh/video kèm theo đánh giá (lưu trữ trong MinIO).

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MySQL
- **Object Storage:** MinIO
- **Messaging:** Apache Kafka

## 🔌 Cấu hình
- **Port:** `8087`

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MySQL
- MinIO
- Kafka

### Lệnh chạy
```bash
mvn spring-boot:run
```
