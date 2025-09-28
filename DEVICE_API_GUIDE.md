# Hướng dẫn API Device và Logout

## Tổng quan
API này hỗ trợ quản lý thiết bị và đăng xuất cho hệ thống ApplyIn, bao gồm:
- Đăng nhập với thông tin thiết bị
- Quản lý multiple devices per user
- Logout từ thiết bị cụ thể
- Logout từ tất cả thiết bị

## Các endpoint mới

### 1. Đăng nhập với Device Info (Cập nhật)
**POST** `/api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "deviceId": "device-unique-id-123",
  "deviceToken": "fcm-token-for-push-notification",
  "platform": "ANDROID",
  "deviceName": "Samsung Galaxy S21",
  "osVersion": "Android 12",
  "appVersion": "1.0.0"
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "jwt-token-here",
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "email": "user@example.com",
    "role": "CANDIDATE",
    "deviceInfo": {
      "deviceId": 1,
      "deviceIdString": "device-unique-id-123",
      "deviceName": "Samsung Galaxy S21",
      "platform": "ANDROID",
      "osVersion": "Android 12",
      "appVersion": "1.0.0",
      "isActive": true
    }
  }
}
```

### 2. Đăng xuất từ thiết bị hiện tại
**POST** `/api/auth/logout`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```
Không cần request body. API sẽ tự động xác định thiết bị cần đăng xuất từ JWT token.
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đăng xuất thành công",
  "data": null
}
```

**Lưu ý:**
- API sẽ tự động lấy thông tin thiết bị từ JWT token trong Authorization header
- Chỉ thiết bị hiện tại (thiết bị đã đăng nhập và tạo ra token) sẽ được đăng xuất
- Không cần truyền deviceId trong request body

### 3. Đăng xuất từ tất cả thiết bị
**POST** `/api/auth/logout-all`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đăng xuất tất cả thiết bị thành công",
  "data": null
}
```

## Device Platform Support

### Các platform được hỗ trợ:
- `ANDROID` - Android devices
- `IOS` - iOS devices  
- `WEB` - Web browsers
- `DESKTOP` - Desktop applications

### Device Information Fields:
- `deviceId`: Unique identifier cho thiết bị (required)
- `deviceToken`: FCM token cho push notification (optional)
- `platform`: Platform của thiết bị (optional, default: WEB)
- `deviceName`: Tên thiết bị (optional)
- `osVersion`: Phiên bản OS (optional)
- `appVersion`: Phiên bản app (optional)

## Database Schema

### Device Table:
```sql
CREATE TABLE devices (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    device_id VARCHAR NOT NULL,
    device_token VARCHAR,
    platform VARCHAR NOT NULL,
    device_name VARCHAR,
    os_version VARCHAR,
    app_version VARCHAR,
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## Business Logic

### 1. Device Management:
- Mỗi user có thể có nhiều devices
- Device được identify bằng `user_id + device_id`
- Khi login với device mới, tạo device record mới
- **Khi login với device cũ, LUÔN cập nhật thông tin device** (deviceToken, platform, deviceName, osVersion, appVersion)
- Thông tin device được cập nhật mỗi lần đăng nhập để đảm bảo dữ liệu luôn mới nhất

### 2. Logout Logic:
- `logout`: Deactivate device cụ thể
- `logout-all`: Deactivate tất cả devices của user
- Device bị deactivate không thể sử dụng để push notification

### 3. Push Notification:
- Chỉ gửi push notification đến devices có `is_active = true`
- Device token được cập nhật mỗi khi login
- Có thể gửi notification đến tất cả devices hoặc device cụ thể

## Error Handling

### Common Errors:

#### 1. Device not found (400):
```json
{
  "statusCode": 400,
  "message": "Thiết bị không tồn tại",
  "data": null
}
```

#### 2. Invalid platform (400):
```json
{
  "statusCode": 400,
  "message": "Platform không hợp lệ",
  "data": null
}
```

## Security Considerations

### 1. Device Token Security:
- Device token chỉ được lưu trữ khi user đăng nhập
- Token được cập nhật mỗi khi login
- Token bị xóa khi logout

### 2. Multiple Device Management:
- User có thể đăng nhập trên nhiều thiết bị
- Mỗi device có thể logout độc lập
- Admin có thể force logout tất cả devices

### 3. JWT Token:
- JWT token vẫn hoạt động bình thường
- Device info được trả về trong login response
- Logout không invalidate JWT token (stateless)

## Testing

### Test Cases:
1. **Login với device mới** - Tạo device record mới
2. **Login với device cũ** - Cập nhật device info
3. **Logout device cụ thể** - Deactivate device
4. **Logout tất cả devices** - Deactivate all devices
5. **Invalid device ID** - Trả về error
6. **Invalid platform** - Fallback to WEB

### Example Test Data:
```json
{
  "email": "test@example.com",
  "password": "password123",
  "deviceId": "android-device-123",
  "deviceToken": "fcm-token-abc123",
  "platform": "ANDROID",
  "deviceName": "Samsung Galaxy S21",
  "osVersion": "Android 12",
  "appVersion": "1.0.0"
}
```

## Future Enhancements

### 1. Device Management API:
- `GET /api/devices` - Lấy danh sách devices
- `DELETE /api/devices/{deviceId}` - Xóa device
- `PUT /api/devices/{deviceId}` - Cập nhật device info

### 2. Push Notification API:
- `POST /api/notifications/send` - Gửi notification
- `POST /api/notifications/send-to-device` - Gửi đến device cụ thể

### 3. Device Analytics:
- Track device usage
- Device performance metrics
- User behavior analytics
