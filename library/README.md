# Common Library

Module **Library** chứa các mã nguồn dùng chung, tiện ích (utilities), DTOs, và các vấn đề xuyên suốt (cross-cutting concerns) được sử dụng bởi nhiều microservices trong dự án E-commerce.

## 📦 Nội dung
- **DTOs:** Các đối tượng chuyển dữ liệu dùng chung.
- **Utils:** Các lớp tiện ích chung (Xử lý chuỗi, ngày tháng, mã hóa).
- **Exceptions:** Xử lý ngoại lệ toàn cục và các lớp ngoại lệ tùy chỉnh.
- **Security:** Các cấu hình bảo mật dùng chung (nếu có).

## 🛠️ Sử dụng
Module này là một dependency cho các dịch vụ khác. Nó không thể chạy độc lập.

### Cài đặt
Để cài đặt thư viện này vào Maven repository cục bộ của bạn để các dịch vụ khác có thể tìm thấy nó:
```bash
mvn clean install
```
