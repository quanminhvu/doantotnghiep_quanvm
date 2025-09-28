# Hướng dẫn Setup SMTP Email

## 1. Cấu hình Environment Variables

### Tạo file `.env` từ `.env-example`:
```bash
cp .env-example .env
```

### Cập nhật file `.env` với thông tin SMTP:

```env
# Mail SMTP Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## 2. Cấu hình cho các nhà cung cấp email phổ biến

### Gmail (Khuyến nghị)
```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

**Lưu ý quan trọng cho Gmail:**
1. Bật 2-Factor Authentication
2. Tạo App Password (không dùng mật khẩu thường)
3. Vào Google Account → Security → 2-Step Verification → App passwords
4. Tạo app password cho "Mail"

### Outlook/Hotmail
```env
MAIL_HOST=smtp-mail.outlook.com
MAIL_PORT=587
MAIL_USERNAME=your-email@outlook.com
MAIL_PASSWORD=your-password
```

### Yahoo Mail
```env
MAIL_HOST=smtp.mail.yahoo.com
MAIL_PORT=587
MAIL_USERNAME=your-email@yahoo.com
MAIL_PASSWORD=your-app-password
```

### Custom SMTP Server
```env
MAIL_HOST=your-smtp-server.com
MAIL_PORT=587
MAIL_USERNAME=your-username
MAIL_PASSWORD=your-password
```

## 3. Các cấu hình SMTP khác

### Port 465 (SSL)
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 465
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: true
          starttls:
            enable: false
```

### Port 25 (Không mã hóa - không khuyến nghị)
```yaml
spring:
  mail:
    host: your-smtp-server.com
    port: 25
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: false
```

## 4. Test cấu hình email

### Tạo file test email:
```java
@SpringBootTest
class EmailTest {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Test
    void testSendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("test@example.com");
        message.setSubject("Test Email");
        message.setText("This is a test email from Spring Boot");
        
        mailSender.send(message);
        System.out.println("Email sent successfully!");
    }
}
```

## 5. Troubleshooting

### Lỗi thường gặp:

#### 1. Authentication failed
```
javax.mail.AuthenticationFailedException: 535-5.7.8 Username and Password not accepted
```
**Giải pháp:**
- Kiểm tra username/password
- Với Gmail: sử dụng App Password thay vì mật khẩu thường
- Bật 2-Factor Authentication

#### 2. Connection timeout
```
java.net.SocketTimeoutException: Read timed out
```
**Giải pháp:**
- Kiểm tra firewall
- Thử port khác (465 thay vì 587)
- Kiểm tra network connection

#### 3. SSL/TLS errors
```
javax.net.ssl.SSLException: Unsupported or unrecognized SSL message
```
**Giải pháp:**
- Kiểm tra cấu hình SSL/TLS
- Thử port 465 với SSL thay vì 587 với STARTTLS

## 6. Bảo mật

### Không commit file .env
```gitignore
.env
*.env
```

### Sử dụng environment variables trong production:
```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

### Docker environment:
```yaml
environment:
  - MAIL_HOST=smtp.gmail.com
  - MAIL_PORT=587
  - MAIL_USERNAME=your-email@gmail.com
  - MAIL_PASSWORD=your-app-password
```

## 7. Monitoring và Logging

### Enable mail debug:
```yaml
spring:
  mail:
    properties:
      mail:
        debug: true
```

### Log email sending:
```java
@Slf4j
public class EmailService {
    
    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}, error: {}", to, e.getMessage());
            throw e;
        }
    }
}
```
