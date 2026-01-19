# Auth Service

**Auth Service** quản lý danh tính, xác thực và phân quyền cho nền tảng E-commerce. Nó tích hợp với **Keycloak** để xử lý phiên người dùng và token (JWT).

## 🛡️ Tính năng Chính
- **Xác thực Người dùng:** Xử lý Đăng nhập/Đăng xuất.
- **Quản lý Token:** Cấp và xác thực JWT access tokens.
- **Tích hợp:** Kết nối với Keycloak để quản lý danh tính (IAM).
- **Giao tiếp:** Sử dụng Kafka cho các sự kiện liên quan đến xác thực.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Bảo mật:** Spring Security, OAuth2 Resource Server
- **IAM:** Keycloak
- **Cơ sở dữ liệu:** MySQL
- **Messaging:** Apache Kafka

## 🔌 API Endpoints
Base URL: `/api/v1/auth`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/login` | Đăng nhập hệ thống, trả về Access Token. |
| `POST` | `/logout` | Đăng xuất, xóa cookie phiên làm việc. |
| `POST` | `/refresh-token` | Làm mới Access Token khi hết hạn. |
| `PATCH` | `/` | Cập nhật thông tin tài khoản (cá nhân). |
| `PATCH` | `/{userId}` | (Admin) Cập nhật trạng thái tài khoản người dùng. |

## ⚙️ Cấu hình (Environment Variables)

Dưới đây là các biến môi trường quan trọng cần cấu hình trong `.env` hoặc `application.yml`:

| Biến | Mô tả | Ví dụ |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port của dịch vụ | `8081` |
| `KEYCLOAK_SERVER_URL` | URL của Keycloak Server | `http://localhost:18080` |
| `KEYCLOAK_REALM` | Realm trong Keycloak | `ecommerce` |
| `KEYCLOAK_CLIENT_ID` | Client ID cho Auth Service | `ecommerce-client` |
| `KEYCLOAK_CLIENT_SECRET` | Client Secret (để trao đổi token) | `********` |
| `MYSQL_URL` | JDBC URL kết nối MySQL | `jdbc:mysql://localhost:3306/auth_db` |
| `KAFKA_BROKERS` | Địa chỉ Kafka Broker | `localhost:9092` |

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- Keycloak phải đang chạy trên port `18080`.
- Kafka và MySQL phải khả dụng.

### Lệnh chạy
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```
