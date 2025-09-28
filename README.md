## Applyin API Specification

### Overview
- Base URL: `http://localhost:8080`
- Auth: Bearer JWT via `Authorization: Bearer <token>`
- Roles: `ADMIN`, `RECRUITER`, `CANDIDATE`

### Environment
App reads values from `.env` (via spring-dotenv) and `application.yml` fallback.

Required variables:
```bash
DB_URL=jdbc:postgresql://localhost:5432/applyin
DB_USERNAME=...
DB_PASSWORD=...
JWT_SECRET=Base64_256bit_key
JWT_EXPIRATION_MS=3600000
```

### Authentication

1) Register
- POST `/api/auth/register`
- Access: Public
- Notes:
  - Default role is `CANDIDATE`.
  - Non-admin may request `RECRUITER` by passing `role: "RECRUITER"`.
  - Only `ADMIN` can assign any role (ADMIN/RECRUITER/CANDIDATE).
- Request body:
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "123456",
  "role": "RECRUITER"
}
```
- Responses:
  - 200 OK (empty)
  - 400 Bad Request (validation)
  - 409 Conflict (email exists)

2) Login
- POST `/api/auth/login`
- Access: Public
- Request:
```json
{ "email": "john@example.com", "password": "123456" }
```
- Response 200:
```json
{ "accessToken": "<jwt>" }
```

3) Forgot Password
- POST `/api/auth/forgot-password`
- Access: Public
- Request:
```json
{ "email": "john@example.com" }
```
- Response: 200 OK (empty)

4) Reset Password
- POST `/api/auth/reset-password`
- Access: Public
- Request:
```json
{ "token": "<reset-token>", "newPassword": "newpass123" }
```
- Response: 200 OK (empty)

### Users

1) Get current user
- GET `/api/users/me`
- Access: `ADMIN | RECRUITER | CANDIDATE`
- Headers:
  - `Authorization: Bearer <jwt>`
- Response 200:
```json
{ "id": 1, "fullName": "John Doe", "email": "john@example.com", "role": "CANDIDATE" }
```

### Error Codes
- 400 Bad Request: Invalid input
- 401 Unauthorized: Missing/invalid token
- 403 Forbidden: Insufficient role
- 404 Not Found
- 409 Conflict: Resource conflict

### Run Locally
```bash
# 1) Create .env (see Environment section)
# 2) Start Postgres (local or docker)

./mvnw spring-boot:run

# or build then run
./mvnw clean package
java -jar target/applyin-0.0.1-SNAPSHOT.jar
```

### Notes
- JWT secret must be a Base64-encoded 256-bit key in production.
- Emails are sent via `JavaMailSender` if configured; otherwise sending is skipped gracefully.


