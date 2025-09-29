# Hướng dẫn API User Profile

## Tổng quan
API này hỗ trợ quản lý thông tin profile của user, bao gồm:
- Lấy thông tin profile chi tiết
- Cập nhật thông tin profile
- Upload avatar và CV

## Các endpoint

### 1. Lấy thông tin profile chi tiết
**GET** `/api/users/profile`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Lấy thông tin profile thành công",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "email": "user@example.com",
    "role": "CANDIDATE",
    "avatarUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/avatar.jpg",
    "cvUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/cv.pdf",
    "phoneNumber": "0123456789",
    "bio": "Software Developer with 3 years experience",
    "dateOfBirth": "1990-01-01T00:00:00Z",
    "address": "123 Main St, Ho Chi Minh City",
    "linkedinUrl": "https://linkedin.com/in/user",
    "githubUrl": "https://github.com/user",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 2. Cập nhật thông tin profile (chỉ dữ liệu)
**PUT** `/api/users/profile`

**Headers:**
```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A Updated",
  "phoneNumber": "0987654321",
  "bio": "Senior Software Developer with 5 years experience",
  "dateOfBirth": "1990-01-01T00:00:00Z",
  "address": "456 New St, Ho Chi Minh City",
  "linkedinUrl": "https://linkedin.com/in/newuser",
  "githubUrl": "https://github.com/newuser",
  "avatarUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/new-avatar.jpg",
  "cvUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/new-cv.pdf"
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Cập nhật profile thành công",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A Updated",
    "email": "user@example.com",
    "role": "CANDIDATE",
    "avatarUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/new-avatar.jpg",
    "cvUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/new-cv.pdf",
    "phoneNumber": "0987654321",
    "bio": "Senior Software Developer with 5 years experience",
    "dateOfBirth": "1990-01-01T00:00:00Z",
    "address": "456 New St, Ho Chi Minh City",
    "linkedinUrl": "https://linkedin.com/in/newuser",
    "githubUrl": "https://github.com/newuser",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T12:00:00Z"
  }
}
```

### 3. Cập nhật profile với upload files
**PUT** `/api/users/profile/with-files`

**Headers:**
```
Authorization: Bearer <jwt-token>
Content-Type: multipart/form-data
```

**Request Body (Form Data):**
- `avatar` (file, optional): File ảnh avatar (jpg, png, gif)
- `cv` (file, optional): File CV (pdf, doc, docx)
- `profileData` (string, required): JSON string chứa thông tin profile

**profileData JSON:**
```json
{
  "fullName": "Nguyễn Văn A Updated",
  "phoneNumber": "0987654321",
  "bio": "Senior Software Developer with 5 years experience",
  "dateOfBirth": "1990-01-01T00:00:00Z",
  "address": "456 New St, Ho Chi Minh City",
  "linkedinUrl": "https://linkedin.com/in/newuser",
  "githubUrl": "https://github.com/newuser"
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Cập nhật profile với files thành công",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A Updated",
    "email": "user@example.com",
    "role": "CANDIDATE",
    "avatarUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/uuid-avatar.jpg",
    "cvUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/uuid-cv.pdf",
    "phoneNumber": "0987654321",
    "bio": "Senior Software Developer with 5 years experience",
    "dateOfBirth": "1990-01-01T00:00:00Z",
    "address": "456 New St, Ho Chi Minh City",
    "linkedinUrl": "https://linkedin.com/in/newuser",
    "githubUrl": "https://github.com/newuser",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T12:00:00Z"
  }
}
```

### 4. Upload File (API cập nhật)
**POST** `/upload`

**Request Body (Form Data):**
- `file` (file, required): File cần upload

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Upload thành công",
  "data": {
    "fileName": "original-filename.jpg",
    "fileUrl": "https://pub-ef123f45210b4bcb9ae39d76b2685847.r2.dev/uuid-filename.jpg",
    "fileSize": 1024000
  }
}
```

**Response Error (400):**
```json
{
  "statusCode": 400,
  "message": "File không được để trống",
  "data": null
}
```

**Response Error (500):**
```json
{
  "statusCode": 500,
  "message": "Lỗi upload: Unable to upload file",
  "data": null
}
```

## Profile Fields

### Thông tin cơ bản:
- `fullName`: Họ và tên (string)
- `email`: Email (string, readonly)
- `role`: Vai trò (string, readonly)

### Thông tin profile:
- `avatarUrl`: URL ảnh đại diện (string, optional)
- `cvUrl`: URL file CV (string, optional)
- `phoneNumber`: Số điện thoại (string, optional)
- `bio`: Mô tả bản thân (string, optional)
- `dateOfBirth`: Ngày sinh (ISO 8601 datetime, optional)
- `address`: Địa chỉ (string, optional)
- `linkedinUrl`: LinkedIn URL (string, optional)
- `githubUrl`: GitHub URL (string, optional)

### Metadata:
- `createdAt`: Thời gian tạo (ISO 8601 datetime)
- `updatedAt`: Thời gian cập nhật cuối (ISO 8601 datetime)

## Business Logic

### 1. Profile Management:
- Mỗi user có thể có một profile (one-to-one relationship)
- Profile được tạo tự động khi user cập nhật lần đầu
- Tất cả fields trong profile đều optional (trừ user relationship)

### 2. File Upload Logic:
- Files được upload lên Cloudflare R2 storage
- Tên file được tạo unique bằng UUID để tránh conflict
- Hỗ trợ upload đồng thời avatar và CV trong một request
- File URLs được lưu trong database

### 3. Update Logic:
- Chỉ cập nhật các fields được cung cấp (null/empty sẽ được bỏ qua)
- `fullName` được cập nhật trong bảng `users`
- Các thông tin khác được cập nhật trong bảng `profile_users`
- Tự động cập nhật `updatedAt` timestamp

## Database Schema

### ProfileUser Table:
```sql
CREATE TABLE profile_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) UNIQUE,
    avatar_url VARCHAR,
    cv_url VARCHAR,
    phone_number VARCHAR,
    bio TEXT,
    date_of_birth TIMESTAMP,
    address VARCHAR,
    linkedin_url VARCHAR,
    github_url VARCHAR,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## Error Handling

### Common Errors:

#### 1. Unauthorized (401):
```json
{
  "statusCode": 401,
  "message": "Unauthorized",
  "data": null
}
```

#### 2. User not found (404):
```json
{
  "statusCode": 404,
  "message": "Email không tồn tại trong hệ thống",
  "data": null
}
```

#### 3. Invalid file format (400):
```json
{
  "statusCode": 400,
  "message": "File không được để trống",
  "data": null
}
```

#### 4. File upload error (500):
```json
{
  "statusCode": 500,
  "message": "Lỗi upload: Unable to upload file",
  "data": null
}
```
