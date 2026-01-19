# Flash Sale Service

**Flash Sale Service** được thiết kế để xử lý lưu lượng truy cập lớn trong các sự kiện flash sale. Nó quản lý các ưu đãi trong thời gian giới hạn và đảm bảo không bán quá số lượng (oversell) khi tải cao.

## ⚡ Tính năng Chính
- **Quản lý Flash Sale:** Tạo và lên lịch các đợt flash sale.
- **Hiệu suất Cao:** Tối ưu hóa cho lưu lượng truy cập lớn và trừ kho nhanh chóng.
- **Bảo vệ Tồn kho:** Ngăn chặn bán quá (overselling) sử dụng Redis hoặc optimistic locking.
- **Hàng đợi:** Có thể sử dụng Kafka để đệm (buffer) các yêu cầu.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MySQL
- **Cache:** Redis (thường dùng cho bộ đếm tồn kho)
- **Messaging:** Apache Kafka

## 🔌 Cấu hình
- **Port:** `8090`

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MySQL
- Redis
- Kafka

### Lệnh chạy
```bash
mvn spring-boot:run
```
