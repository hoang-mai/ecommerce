# Payment Service

**Payment Service** xử lý các hoạt động thanh toán. Nó tích hợp với các cổng thanh toán bên ngoài và quản lý hồ sơ giao dịch.

## 💳 Tính năng Chính
- **Xử lý Thanh toán:** Xử lý yêu cầu thanh toán cho đơn hàng.
- **Hoàn tiền:** Xử lý hoàn tiền (hủy bỏ).
- **Lịch sử Giao dịch:** Nhật ký chi tiết các lần thử thanh toán.
- **Publish Sự kiện:** Thông báo cho Order/Saga service khi thanh toán thành công/thất bại.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MySQL
- **Messaging:** Apache Kafka

## 🔌 Cấu hình
- **Port:** `8084`

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MySQL
- Kafka

### Lệnh chạy
```bash
mvn spring-boot:run
```
