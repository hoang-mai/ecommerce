# Saga Service

**Saga Service** đóng vai trò là nhạc trưởng (orchestrator) cho các giao dịch phân tán giữa các microservices. Nó triển khai Saga pattern để đảm bảo tính nhất quán dữ liệu trong các quy trình nghiệp vụ dài hạn (như "Thanh toán" hoặc "Hủy đơn hàng").

## 🔄 Tính năng Chính
- **Điều phối (Orchestration):** Điều phối các bước trong giao dịch phân tán.
- **Bồi thường (Compensation):** Kích hoạt các giao dịch bồi thường (rollback) nếu một bước thất bại.
- **Quản lý Workflow:** Sử dụng Temporal.io cho việc thực thi bền vững (durable execution).

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Orchestration Engine:** Temporal
- **Messaging:** Apache Kafka

## 🔌 Cấu hình
- **Port:** `8088`

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- Temporal Server
- Kafka

### Lệnh chạy
```bash
mvn spring-boot:run
```
