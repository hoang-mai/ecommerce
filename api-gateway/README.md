# API Gateway

**API Gateway** đóng vai trò là điểm truy cập duy nhất cho tất cả các yêu cầu từ client trong kiến trúc microservices E-commerce. Nó xử lý định tuyến (routing), cân bằng tải (load balancing) và các vấn đề xuyên suốt (cross-cutting concerns) như xác thực, giới hạn tốc độ (rate limiting).

## 🔑 Tính năng Chính
- **Định tuyến (Routing):** Chuyển tiếp các request đến microservices phù hợp (user, product, order, v.v.).
- **Cân bằng tải (Load Balancing):** Phân phối lưu lượng truy cập qua các instance của dịch vụ.
- **Bảo mật:** Tích hợp với Auth Service/Keycloak để kiểm tra token trước khi forward request.
- **Tài liệu:** Tổng hợp các định nghĩa Swagger/OpenAPI từ các service con.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Gateway:** Spring Cloud Gateway
- **Discovery:** Spring Cloud Netflix Eureka (nếu có) hoặc Kubernetes/Docker DNS

## 🛣️ Định tuyến (Routes)

Các route chính được cấu hình trong Gateway:

| Path Prefix | Target Service | Mô tả |
| :--- | :--- | :--- |
| `/api/v1/auth/**` | `auth-service` | Các API xác thực (Login, Logout). |
| `/api/v1/users/**` | `user-service` | Quản lý người dùng. |
| `/api/v1/products/**` | `product-service` | Quản lý sản phẩm. |
| `/api/v1/orders/**` | `order-service` | Quản lý đơn hàng. |
| `/api/v1/payments/**` | `payment-service` | Xử lý thanh toán. |
| `/api/v1/reviews/**` | `review-service` | Đánh giá sản phẩm. |
| `/api/v1/cart/**` | `order-service` | Quản lý giỏ hàng (nếu tách biệt). |

## ⚙️ Cấu hình (Environment Variables)

| Biến | Mô tả | Ví dụ |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port của Gateway | `8080` |
| `AUTH_SERVICE_URI` | URI của Auth Service | `lb://auth-service` |
| `USER_SERVICE_URI` | URI của User Service | `lb://user-service` |
# (Các biến URI khác tương tự cho từng service)

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- Java 17
- Maven
- Các dịch vụ cốt lõi (Auth, User, v.v.) phải đang chạy để có đầy đủ chức năng.

### Lệnh chạy
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```
