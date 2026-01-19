# Proto Module

Module **Proto** chứa các định nghĩa Protocol Buffer (`.proto`) được sử dụng cho giao tiếp gRPC giữa các microservices.

## 📄 Nội dung
- **Protobuf Definitions:** Các giao diện dịch vụ và loại tin nhắn.
- **Generated Code:** Mã Java được tạo từ các file `.proto` (sau khi build).

## 🛠️ Sử dụng
Module này là một dependency cho các dịch vụ giao tiếp qua gRPC.

### Build & Generate
Để biên dịch các file `.proto` và tạo mã nguồn Java:
```bash
mvn clean install
```
