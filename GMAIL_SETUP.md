# Hướng dẫn Setup Gmail SMTP

## Bước 1: Tạo App Password cho Gmail

### 1.1. Bật 2-Factor Authentication
1. Đăng nhập vào [Google Account](https://myaccount.google.com/)
2. Vào **Security** → **2-Step Verification**
3. Bật 2-Step Verification nếu chưa có

### 1.2. Tạo App Password
1. Vào **Security** → **2-Step Verification** → **App passwords**
2. Chọn **Mail** và **Other (Custom name)**
3. Nhập tên: "ApplyIn App"
4. Google sẽ tạo ra 16 ký tự password (ví dụ: `abcd efgh ijkl mnop`)
5. **Lưu lại password này** - chỉ hiển thị 1 lần!

## Bước 2: Cấu hình Environment Variables

### 2.1. Tạo file `.env`:
```bash
cp .env-example .env
```

### 2.2. Cập nhật file `.env`:
```env
# Gmail SMTP Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=abcd efgh ijkl mnop
```

**Lưu ý:** 
- `MAIL_USERNAME`: Email Gmail của bạn
- `MAIL_PASSWORD`: App password 16 ký tự (không có dấu cách)

## Bước 3: Test cấu hình

### 3.1. Chạy ứng dụng:
```bash
./mvnw spring-boot:run
```

### 3.2. Test API quên mật khẩu:
```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}'
```

### 3.3. Kiểm tra email:
- Kiểm tra hộp thư đến của email đã đăng ký
- Kiểm tra thư mục Spam nếu không thấy

## Bước 4: Troubleshooting

### Lỗi "Authentication failed":
```
535-5.7.8 Username and Password not accepted
```
**Giải pháp:**
- Kiểm tra App Password (16 ký tự, không có dấu cách)
- Đảm bảo 2-Factor Authentication đã bật
- Thử tạo App Password mới

### Lỗi "Less secure app access":
```
535-5.7.8 Username and Password not accepted
```
**Giải pháp:**
- Không cần bật "Less secure app access" nữa
- Sử dụng App Password thay vì mật khẩu thường

### Lỗi "Connection timeout":
```
java.net.SocketTimeoutException: Read timed out
```
**Giải pháp:**
- Kiểm tra firewall
- Thử port 465 với SSL:
```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=465
```

## Bước 5: Cấu hình Production

### 5.1. Environment Variables:
```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

### 5.2. Docker:
```yaml
environment:
  - MAIL_HOST=smtp.gmail.com
  - MAIL_PORT=587
  - MAIL_USERNAME=your-email@gmail.com
  - MAIL_PASSWORD=your-app-password
```

### 5.3. Heroku:
```bash
heroku config:set MAIL_HOST=smtp.gmail.com
heroku config:set MAIL_PORT=587
heroku config:set MAIL_USERNAME=your-email@gmail.com
heroku config:set MAIL_PASSWORD=your-app-password
```

## Bước 6: Bảo mật

### 6.1. Không commit file .env:
```gitignore
.env
*.env
```

### 6.2. Sử dụng secrets management:
- AWS Secrets Manager
- Azure Key Vault
- HashiCorp Vault

### 6.3. Rotate App Passwords:
- Thay đổi App Password định kỳ
- Xóa App Password cũ khi không dùng

## Ví dụ cấu hình hoàn chỉnh:

### File `.env`:
```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/applyin
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=VGhpc0lzQVN1cGVyU2VjcmV0S2V5VGhhdFNob3VsZEJlMzJCeXRlc0xvbmc=
JWT_EXPIRATION_MS=3600000

# Gmail SMTP
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=myapp@gmail.com
MAIL_PASSWORD=abcd efgh ijkl mnop
```

### File `application.yml`:
```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          connectiontimeout: 5000
          timeout: 3000
          writetimeout: 5000
```
