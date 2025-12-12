# Order Service

Service quản lý giỏ hàng và đơn hàng trong hệ thống ecommerce.

## Entities

### Cart
- `cartId`: ID giỏ hàng (Primary Key)
- `userId`: ID người dùng (Unique)
- `cartItems`: Danh sách các item trong giỏ hàng

### CartItem
- `cartItemId`: ID của item trong giỏ hàng (Primary Key)
- `cart`: Giỏ hàng chứa item này
- `productId`: ID sản phẩm
- `productVariantId`: ID biến thể sản phẩm
- `quantity`: Số lượng
- `price`: Giá
- `shopId`: ID shop

### Order
- `orderId`: ID đơn hàng (Primary Key)
- `userId`: ID người dùng
- `status`: Trạng thái đơn hàng (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- `totalPrice`: Tổng giá trị đơn hàng
- `paymentId`: ID thanh toán
- `shippingAddressId`: ID địa chỉ giao hàng
- `items`: Danh sách các item trong đơn hàng

### OrderItem
- `orderItemId`: ID của item trong đơn hàng (Primary Key)
- `order`: Đơn hàng chứa item này
- `productId`: ID sản phẩm
- `quantity`: Số lượng
- `price`: Giá

## API Endpoints

### Cart APIs

#### 1. Get Cart
```http
GET /api/cart
Authorization: Bearer {token}
```
**Response:**
```json
{
  "statusCode": 200,
  "message": "Cart retrieved successfully",
  "data": {
    "cartId": 1,
    "userId": 123,
    "cartItems": [
      {
        "cartItemId": 1,
        "productId": 10,
        "productVariantId": 15,
        "quantity": 2,
        "price": 100000,
        "subtotal": 200000,
        "shopId": 5
      }
    ],
    "totalAmount": 200000
  }
}
```

#### 2. Add Item to Cart
```http
POST /api/cart/items
Authorization: Bearer {token}
Content-Type: application/json

{
  "productId": 10,
  "productVariantId": 15,
  "quantity": 2,
  "shopId": 5
}
```

#### 3. Update Cart Item Quantity
```http
PATCH /api/cart/items/{cartItemId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "quantity": 3
}
```

#### 4. Remove Cart Item
```http
DELETE /api/cart/items/{cartItemId}
Authorization: Bearer {token}
```

#### 5. Clear Cart
```http
DELETE /api/cart
Authorization: Bearer {token}
```

### Order APIs

#### 1. Create Order
```http
POST /api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "shippingAddressId": "123e4567-e89b-12d3-a456-426614174000",
  "items": [
    {
      "productId": 10,
      "productVariantId": 15,
      "quantity": 2,
      "price": 100000
    }
  ]
}
```

#### 2. Get Order by ID
```http
GET /api/orders/{orderId}
Authorization: Bearer {token}
```

#### 3. Get User Orders
```http
GET /api/orders
Authorization: Bearer {token}

# With status filter
GET /api/orders?status=PENDING
```

#### 4. Update Order Status
```http
PATCH /api/orders/{orderId}/status?status=PROCESSING
Authorization: Bearer {token}
```

#### 5. Cancel Order
```http
POST /api/orders/{orderId}/cancel
Authorization: Bearer {token}
```

## Order Status Flow
```
PENDING → PROCESSING → SHIPPED → DELIVERED
   ↓
CANCELLED
```

## Database Configuration

Database: `order_db`
- Host: localhost:3306
- Username: root
- Password: root

## Running the Service

1. Start MySQL database
2. Run the application:
```bash
cd order_service
mvn spring-boot:run
```

Service will start on port **8084**

## Swagger Documentation

Access API documentation at:
```
http://localhost:8084/swagger-ui.html
```

## Features

✅ **Cart Management**
- Mỗi user có 1 giỏ hàng duy nhất
- Tự động tạo giỏ hàng khi user thêm item lần đầu
- Tự động cập nhật số lượng nếu item đã tồn tại
- Tính toán tổng giá trị giỏ hàng

✅ **Order Management**
- Tạo đơn hàng từ danh sách items
- Theo dõi trạng thái đơn hàng
- Hủy đơn hàng (nếu chưa giao)
- Xem lịch sử đơn hàng

✅ **Security**
- JWT authentication với Keycloak
- User chỉ truy cập được giỏ hàng và đơn hàng của mình

✅ **Service Discovery**
- Đăng ký với Eureka Server
- Có thể gọi từ API Gateway

## TODO

- [ ] Tích hợp với Product Service để lấy giá sản phẩm real-time
- [ ] Tích hợp với Inventory Service để kiểm tra tồn kho
- [ ] Tích hợp với Payment Service để xử lý thanh toán
- [ ] Tích hợp với Notification Service để gửi thông báo
- [ ] Implement Kafka events cho order lifecycle
- [ ] Add caching với Redis

