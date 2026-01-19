# Product Service

**Product Service** chịu trách nhiệm quản lý danh mục sản phẩm, bao gồm tạo sản phẩm, quản lý hàng tồn kho (stock), và phân loại các mặt hàng.

## 📦 Tính năng Chính
- **Quản lý Danh mục:** Thêm, sửa, xóa sản phẩm.
- **Theo dõi Tồn kho:** Quản lý số lượng hàng trong kho.
- **Danh mục (Categories):** Tổ chức sản phẩm vào các danh mục.
- **Hình ảnh:** Xử lý hình ảnh sản phẩm sử dụng MinIO.
- **Sự kiện:** Publish các cập nhật sản phẩm lên Kafka để đánh chỉ mục tìm kiếm (Read Service).

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MySQL
- **Messaging:** Apache Kafka
- **Object Storage:** MinIO

## 🔌 API Endpoints
Base URL: `/api/v1/products`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/` | Tạo mới sản phẩm (bao gồm upload ảnh). |
| `PATCH` | `/{productId}` | Cập nhật thông tin sản phẩm và ảnh. |
| `PATCH` | `/{productId}/product-status` | Thay đổi trạng thái sản phẩm (ACTIVE/INACTIVE). |
| `PATCH` | `/{variantId}/status` | Cập nhật trạng thái của biến thể sản phẩm (Variant). |

## ⚙️ Cấu hình (Environment Variables)

| Biến | Mô tả | Ví dụ |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port của dịch vụ | `8085` |
| `MYSQL_URL` | JDBC URL kết nối MySQL | `jdbc:mysql://localhost:3306/product_db` |
| `KAFKA_BROKERS` | Địa chỉ Kafka Broker | `localhost:9092` |
| `MINIO_ENDPOINT` | URL của MinIO Server | `http://localhost:9000` |
| `MINIO_BUCKET` | Tên bucket | `ecommerce` |

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MySQL Database
- Kafka
- MinIO

### Lệnh chạy
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```
