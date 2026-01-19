# Order Service

**Order Service** quản lý vòng đời của đơn hàng, từ khi tạo đến khi hoàn tất. Nó phối hợp với các dịch vụ khác để kiểm tra tồn kho, xử lý thanh toán và cập nhật trạng thái đơn hàng.

## 🛒 Tính năng Chính
- **Tạo đơn hàng:** Xác thực giỏ hàng và tạo đơn hàng.
- **Quản lý Trạng thái:** Theo dõi trạng thái đơn hàng (PENDING, PAID, SHIPPED, v.v.).
- **Lịch sử:** Truy xuất lịch sử đơn hàng cho người dùng.
- **Tích hợp Saga:** Tham gia vào các giao dịch phân tán (Saga) để đảm bảo tính nhất quán của đơn hàng.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MySQL
- **Messaging:** Apache Kafka
- **Điều phối (Orchestration):** Temporal (thông qua Saga Service)

## 🔌 API Endpoints
Base URL: `/api/v1/orders`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/` | Tạo đơn hàng mới từ giỏ hàng. |
| `PATCH` | `/{orderId}/status` | Cập nhật trạng thái đơn hàng. |

## ⚙️ Cấu hình (Environment Variables)

| Biến | Mô tả | Ví dụ |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port của dịch vụ | `8083` |
| `MYSQL_URL` | JDBC URL kết nối MySQL | `jdbc:mysql://localhost:3306/order_db` |
| `KAFKA_BROKERS` | Địa chỉ Kafka Broker | `localhost:9092` |
| `TEMPORAL_TARGET` | Địa chỉ Temporal Server | `localhost:7233` |

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MySQL Database
- Kafka
- Saga Service & Temporal

### Lệnh chạy
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```
