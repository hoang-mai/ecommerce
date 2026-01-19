# Read Service

**Read Service** là một phần của pattern CQRS (Command Query Responsibility Segregation). Nó được tối ưu hóa cho việc đọc và tìm kiếm dữ liệu hiệu suất cao, dữ liệu này được ghi bởi các dịch vụ khác (như Product hoặc Order service).

## 🔍 Tính năng Chính
- **Tìm kiếm Nâng cao:** Tìm kiếm Full-text sử dụng Elasticsearch.
- **Dữ liệu Tổng hợp:** Phục vụ các view kết hợp dữ liệu từ nhiều domain.
- **Truy xuất Nhanh:** Tối ưu hóa cho các hoạt động đọc nhiều (read-heavy).

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3.x
- **Cơ sở dữ liệu:** MongoDB (Lưu trữ Read model)
- **Công cụ Tìm kiếm:** Elasticsearch
- **Messaging:** Apache Kafka (tiêu thụ các thay đổi để cập nhật read models)

## 🔌 Cấu hình
- **Port:** `8086`

## 🚀 Chạy Dịch vụ

### Yêu cầu Tiên quyết
- MongoDB
- Elasticsearch
- Kafka

### Lệnh chạy
```bash
mvn spring-boot:run
```
