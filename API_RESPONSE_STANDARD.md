# Chuẩn API Response cho ApplyIn

## Tổng quan
Tất cả API endpoints trong hệ thống ApplyIn đều sử dụng `ApiResponse<T>` làm response chuẩn để đảm bảo tính nhất quán.

## Cấu trúc ApiResponse

```java
public class ApiResponse<T> {
    private int statusCode;    // HTTP status code
    private String message;    // Message mô tả kết quả
    private T data;           // Data trả về (có thể null)
}
```

## Cách sử dụng

### 1. Response thành công
```java
// Với data
return ResponseEntity.ok(ApiResponse.ok("Thành công", data));

// Không có data
return ResponseEntity.ok(ApiResponse.ok("Thành công", null));
```

### 2. Response lỗi (trong controller)
```java
// Lỗi client (400)
return ResponseEntity.badRequest()
    .body(ApiResponse.error(400, "Dữ liệu không hợp lệ"));

// Lỗi server (500)
return ResponseEntity.status(500)
    .body(ApiResponse.error(500, "Lỗi hệ thống"));
```

### 3. Sử dụng GlobalExceptionHandler
Controllers chỉ cần throw exception, GlobalExceptionHandler sẽ tự động xử lý và trả về ApiResponse:

```java
@PostMapping("/example")
public ResponseEntity<ApiResponse<DataType>> example(@RequestBody RequestType request) {
    // Không cần try-catch, để GlobalExceptionHandler xử lý
    DataType result = service.processData(request);
    return ResponseEntity.ok(ApiResponse.ok("Xử lý thành công", result));
}
```

## Các loại response chuẩn

### Success Responses (2xx)
```json
{
  "statusCode": 200,
  "message": "Thành công",
  "data": { ... }
}
```

### Client Error Responses (4xx)
```json
{
  "statusCode": 400,
  "message": "Dữ liệu không hợp lệ",
  "data": {
    "field1": "Lỗi validation field1",
    "field2": "Lỗi validation field2"
  }
}
```

### Server Error Responses (5xx)
```json
{
  "statusCode": 500,
  "message": "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.",
  "data": null
}
```

## Danh sách Controllers đã chuẩn hóa

### ✅ AuthController
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/logout` - Đăng xuất
- `POST /api/auth/logout-all` - Đăng xuất tất cả thiết bị
- `POST /api/auth/forgot-password` - Quên mật khẩu
- `POST /api/auth/verify-otp` - Xác thực OTP
- `POST /api/auth/reset-password` - Đặt lại mật khẩu

### ✅ UserController
- `GET /api/users/me` - Lấy thông tin cơ bản
- `GET /api/users/profile` - Lấy thông tin profile chi tiết
- `PUT /api/users/profile` - Cập nhật profile
- `PUT /api/users/profile/with-files` - Cập nhật profile với files

### ✅ UploadController
- `POST /upload` - Upload file

## GlobalExceptionHandler

Xử lý tự động các exceptions và trả về ApiResponse:

| Exception | Status Code | Message |
|-----------|-------------|---------|
| `IllegalArgumentException` | 400 | Tùy theo exception message |
| `EmailAlreadyExistsException` | 400 | Email đã tồn tại |
| `UserNotFoundException` | 404 | Email không tồn tại trong hệ thống |
| `InvalidTokenException` | 400 | Token không hợp lệ |
| `OtpExpiredException` | 400 | OTP đã hết hạn |
| `OtpNotVerifiedException` | 400 | OTP chưa được xác thực |
| `BadCredentialsException` | 401 | Email hoặc mật khẩu không đúng |
| `AccessDeniedException` | 403 | Không có quyền truy cập |
| `MethodArgumentNotValidException` | 400 | Dữ liệu không hợp lệ + chi tiết lỗi |
| `MaxUploadSizeExceededException` | 413 | File upload quá lớn |
| `IOException` | 500 | Lỗi xử lý file |
| `Exception` | 500 | Lỗi hệ thống |

## Best Practices

### 1. Message Guidelines
- Sử dụng tiếng Việt có dấu
- Message ngắn gọn, rõ ràng
- Phù hợp với ngữ cảnh business

### 2. Error Handling
- Không sử dụng try-catch trong controller nếu không cần thiết
- Để GlobalExceptionHandler xử lý các exceptions
- Chỉ catch exception khi cần xử lý logic đặc biệt

### 3. Status Codes
- Luôn sử dụng đúng HTTP status code
- 200: Success with data
- 201: Created
- 400: Client error
- 401: Unauthorized
- 403: Forbidden
- 404: Not found
- 413: Payload too large
- 500: Server error

### 4. Data Field
- `null` cho endpoints không trả về data
- Object/Array cho endpoints có data
- Validation errors object cho MethodArgumentNotValidException

## Ví dụ Response thực tế

### Login Success
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
    "deviceInfo": { ... }
  }
}
```

### Validation Error
```json
{
  "statusCode": 400,
  "message": "Dữ liệu không hợp lệ",
  "data": {
    "email": "Email không hợp lệ",
    "password": "Mật khẩu phải có ít nhất 6 ký tự"
  }
}
```

### Upload Success
```json
{
  "statusCode": 200,
  "message": "Upload thành công",
  "data": {
    "fileName": "avatar.jpg",
    "fileUrl": "https://cdn.example.com/uuid-avatar.jpg",
    "fileSize": 1024000
  }
}
```

## Migration Notes

Tất cả controllers hiện tại đã được cập nhật để sử dụng ApiResponse chuẩn. Các controllers mới phải tuân thủ chuẩn này.
