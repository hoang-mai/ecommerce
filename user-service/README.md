# User Service

**User Service** xử lý tất cả các hoạt động liên quan đến người dùng, bao gồm quản lý hồ sơ, lưu trữ địa chỉ và thiết lập cá nhân.

## 👤 Tính năng Chính
- **Quản lý Hồ sơ:** Tạo, cập nhật và lấy thông tin người dùng.
- **Sổ Địa chỉ:** Quản lý địa chỉ giao hàng và thanh toán.
- **Đồng bộ Dữ liệu:** Đồng bộ dữ liệu người dùng với các dịch vụ khác qua Kafka.
- **Lưu trữ:** Lưu trữ ảnh đại diện/hình ảnh người dùng trong MinIO.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MySQL
- **Messaging:** Apache Kafka (User updates)
- **Object Storage:** MinIO (Avatars)
- **Giao tiếp:** gRPC & REST

## 🔌 API Endpoints
Base URL: `/api/v1/users`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/` | Lấy thông tin người dùng hiện tại (dựa trên Token). |
| `GET` | `/{userId}` | Lấy thông tin người dùng theo ID. |
| `PATCH` | `/` | Cập nhật thông tin cá nhân. |
| `POST` | `/avatar` | Upload ảnh đại diện (Multipart File). |
| `GET` | `/search` | Tìm kiếm người dùng theo từ khóa (query). |

## ⚙️ Cấu hình (Environment Variables)

Các biến cấu hình cần thiết để chạy dịch vụ:

| Biến | Mô tả | Ví dụ |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port của dịch vụ | `8089` |
| `MYSQL_URL` | JDBC URL kết nối MySQL | `jdbc:mysql://localhost:3306/user_db` |
| `KAFKA_BROKERS` | Địa chỉ Kafka Broker | `localhost:9092` |
| `MINIO_ENDPOINT` | URL của MinIO Server | `http://localhost:9000` |
| `MINIO_ACCESS_KEY` | Access Key MinIO | `root` |
| `MINIO_SECRET_KEY` | Secret Key MinIO | `password` |
| `MINIO_BUCKET` | Tên bucket để lưu ảnh | `user-images` |

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MySQL Database (`user_db`)
- Kafka Broker
- MinIO Server

### Lệnh chạy
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```
