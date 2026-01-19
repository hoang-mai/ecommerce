# Chat Notification Service

**Chat Notification Service** quản lý giao tiếp thời gian thực giữa người dùng (ví dụ: hỗ trợ khách hàng hoặc user-to-user) và gửi thông báo hệ thống.

## 💬 Tính năng Chính
- **Chat Thời gian thực:** Hỗ trợ nhắn tin (thường dùng WebSocket hoặc tương tự).
- **Thông báo:** Push notifications hoặc cảnh báo hệ thống nội bộ.
- **Lịch sử:** Lưu trữ tin nhắn chat.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MongoDB (tối ưu cho lịch sử chat)
- **Object Storage:** MinIO (đa phương tiện trong chat)
- **Messaging:** Apache Kafka

## 🔌 Cấu hình
- **Port:** `8082`

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MongoDB
- MinIO
- Kafka

### Lệnh chạy
```bash
mvn spring-boot:run
```
