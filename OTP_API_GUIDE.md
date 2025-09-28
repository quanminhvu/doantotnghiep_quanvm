# Hướng dẫn sử dụng API OTP cho chức năng quên mật khẩu

## Tổng quan
API này cung cấp chức năng quên mật khẩu sử dụng OTP (One-Time Password) được gửi qua email.

## Quy trình hoạt động
1. **Bước 1**: Gửi yêu cầu quên mật khẩu → Nhận OTP qua email
2. **Bước 2**: Xác thực OTP → Xác nhận quyền đặt lại mật khẩu
3. **Bước 3**: Đặt lại mật khẩu mới

## Các endpoint

### 1. Gửi OTP qua email
**POST** `/api/auth/forgot-password`

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Mã reset đã được gửi đến email của bạn",
  "data": null
}
```

**Response Error (404):**
```json
{
  "statusCode": 404,
  "message": "Email không tồn tại trong hệ thống",
  "data": null
}
```

### 2. Xác thực OTP
**POST** `/api/auth/verify-otp`

**Request Body:**
```json
{
  "email": "user@example.com",
  "otpCode": "123456"
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Xác thực OTP thành công",
  "data": null
}
```

**Response Error (400):**
```json
{
  "statusCode": 400,
  "message": "Mã OTP không đúng",
  "data": null
}
```

**Response Error (400) - OTP hết hạn:**
```json
{
  "statusCode": 400,
  "message": "Mã OTP đã hết hạn, vui lòng yêu cầu mã mới",
  "data": null
}
```

### 3. Đặt lại mật khẩu
**POST** `/api/auth/reset-password`

**Request Body:**
```json
{
  "email": "user@example.com",
  "newPassword": "newPassword123"
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Mật khẩu đã được đặt lại thành công",
  "data": null
}
```

**Response Error (400) - Chưa xác thực OTP:**
```json
{
  "statusCode": 400,
  "message": "Vui lòng xác thực OTP trước khi đặt lại mật khẩu",
  "data": null
}
```

## Quy tắc OTP
- OTP là mã 6 chữ số
- OTP có thời hạn 5 phút
- Mỗi OTP chỉ có thể sử dụng 1 lần
- Sau khi xác thực thành công, OTP sẽ bị xóa

## Cấu hình Email
Đảm bảo cấu hình email trong `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## Lưu ý bảo mật
- OTP được lưu trữ dưới dạng plain text trong database (có thể mã hóa thêm)
- OTP tự động hết hạn sau 5 phút
- Không thể sử dụng lại OTP đã xác thực
- Email chỉ được gửi nếu địa chỉ email tồn tại trong hệ thống
