# Dự án Microservices E-commerce

Đây là một hệ thống E-commerce phân tán, quy mô lớn, được thiết kế dựa trên kiến trúc Microservices hiện đại. Dự án tập trung vào khả năng mở rộng (scalability), khả năng chịu lỗi (resilience) và tính nhất quán dữ liệu trong môi trường phân tán (collaborative consistency).

## 🏗️ Kiến trúc Hệ thống

Hệ thống hoạt động dựa trên sự phối hợp của nhiều dịch vụ độc lập:

1.  **Client Layer:** Người dùng tương tác thông qua Web Client hoặc Mobile App.
2.  **API Gateway Layer:** `api-gateway` là cổng duy nhất, chịu trách nhiệm xác thực sơ bộ, định tuyến và cân bằng tải.
3.  **Identity Provider:** `auth-service` kết hợp với **Keycloak** để quản lý phiên đăng nhập và phân quyền tập trung (OAuth2/OpenID Connect).
4.  **Core Domain Services:** Các dịch vụ nghiệp vụ cốt lõi như `product`, `order`, `user`, `payment`.
5.  **Event Bus (Kafka):** Các dịch vụ giao tiếp bất đồng bộ qua Kafka để giảm sự phụ thuộc lẫn nhau (Decoupling). Ví dụ: `order-service` bắn sự kiện `OrderCreated`, `notification-service` lắng nghe để gửi email.
6.  **Data Consistency (Saga):** `saga-service` sử dụng **Temporal** để điều phối các giao dịch phức tạp kéo dài qua nhiều dịch vụ (Distributed Transactions), đảm bảo tính toàn vẹn dữ liệu (ACID across services).
7.  **CQRS Pattern:** Dữ liệu ghi (Write) được tách biệt với dữ liệu đọc (Read). `read-service` tổng hợp dữ liệu từ các nguồn khác nhau vào Elasticsearch/MongoDB để phục vụ truy vấn cực nhanh.

## 📦 Danh sách Dịch vụ Chi tiết

| Service | Port | Database | Công nghệ Chính                    |
| :--- | :--- | :--- |:-----------------------------------|
| **Api Gateway** | `8080` | N/A | Spring Cloud Gateway, Netty        |
| **Auth Service** | `8081` | MySQL | Spring Security, Keycloak, JWT     |
| **User Service** | `8089` | MySQL | JPA, Hibernate, MinIO              |
| **Product Service** | `8085` | MySQL | JPA, MinIO                         |
| **Order Service** | `8083` | MySQL | JPA, Temporal Client               |
| **Payment Service** | `8084` | MySQL | VNPay/Stripe (Simulated), Kafka    |
| **Flash Sale Service** | `8090` | MySQL | Kafka                              |
| **Review Service** | `8087` | MySQL | JPA, MinIO                         |
| **Read Service** | `8086` | Mongo, ES | Elasticsearch, Spring Data MongoDB |
| **Chat Notification** | `8082` | MongoDB | Redis, WebSocket, FCM (Firebase)   |
| **Saga Service** | `8088` | Temporal DB | Temporal Workflow, Kafka Streams   |

## 🛠️ Yêu cầu Môi trường

Để chạy dự án, bạn cần thiết lập biến môi trường hoặc file `.env` tại thư mục gốc:

```properties
# Database Passwords
MYSQL_ROOT_PASSWORD=password
MYSQL_PASSWORD=password
MONGO_PASSWORD=rootpassword

# Kafka Access
KAFKA_BROKER=kafka:9092

# Keycloak
KEYCLOAK_SERVER_URL=http://localhost:18080
KEYCLOAK_REALM=ecommerce
KEYCLOAK_CLIENT_ID=ecommerce-client
KEYCLOAK_CLIENT_SECRET=********

# MinIO (Lưu trữ ảnh)
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=root
MINIO_SECRET_KEY=password
MINIO_BUCKET=ecommerce

# Service Discovery (Nếu dùng Eureka)
EUREKA_URI=http://localhost:8761/eureka
```

## ❓ Khắc phục sự cố thường gặp

### 1. Lỗi kết nối Kafka
* **Triệu chứng:** Các service báo lỗi `Connection refused` tới `kafka:9092`.
* **Khắc phục:** Đảm bảo tất cả containers đang chạy trên cùng network `service-network`. Nếu chạy service Java bên ngoài Docker (trên IDE), hãy cấu hình trỏ về `localhost:19092`.

### 2. Temporal không khởi động được
* **Khắc phục:** Temporal cần DB khởi tạo trước. Nếu `temporal-admin-tools` chưa chạy xong migration, hãy restart container `temporal`.

### 3. Keycloak báo lỗi "Realm not found"
* **Khắc phục:** Bạn cần import file cấu hình Realm (`realm-export.json`) vào Keycloak khi khởi động lần đầu, hoặc tạo Realm thủ công.

## 🚀 Hướng dấn Tối ưu (Production Mode)

Để chạy hệ thống với cấu hình tối ưu tài nguyên (giới hạn CPU/RAM), hãy sử dụng file `docker-compose-optimization.yml`. File này áp dụng các giới hạn heap size cho JVM (`-Xmx`) để tránh việc containers chiếm dụng toàn bộ RAM máy chủ.

```bash
docker-compose -f docker-compose-optimization.yml up -d
```
