## RECRUITER API

### Overview
- Base URL: `http://localhost:8080`
- Auth: Bearer JWT via `Authorization: Bearer <token>`
- Access: `RECRUITER` or `ADMIN`
- Prefix: `/api/recruiter`

### Common Notes
- All responses are JSON.
- Validation errors return 400 with details.
- 401 if missing/invalid token. 403 if insufficient role.

---

### Recruiter Profile

#### Get my recruiter profile
- Method: `GET`
- Path: `/api/recruiter/profile`
- Response 200: `RecruiterProfileResponse`
```json
{
  "id": 1,
  "companyName": "Acme Corp",
  "companyWebsite": "https://acme.com",
  "companyAddress": "123 Main St",
  "companySize": "51-200",
  "about": "We build rockets.",
  "logoUrl": "https://.../logo.png",
  "createdAt": "2025-10-15T07:00:00Z",
  "updatedAt": "2025-10-15T07:00:00Z"
}
```

#### Create/Update my recruiter profile
- Method: `PUT`
- Path: `/api/recruiter/profile`
- Body: `RecruiterProfileRequest`
```json
{
  "companyName": "Acme Corp",
  "companyWebsite": "https://acme.com",
  "companyAddress": "123 Main St",
  "companySize": "51-200",
  "about": "We build rockets.",
  "logoUrl": "https://.../logo.png",
  "recruiterName": "Nguyen Van A",
  "recruiterTitle": "Talent Acquisition Lead",
  "recruiterPhone": "+84 912 345 678",
  "recruiterEmail": "recruiter@acme.com",
  "recruiterLinkedin": "https://linkedin.com/in/nguyenvana",
  "recruiterAbout": "10+ years in tech hiring",
  "recruiterAvatarUrl": "https://.../avatar.jpg"
}
```
- Response 200: `RecruiterProfileResponse` (same shape như trên)

#### Upload workflow for images (R2)
- Step 1: Upload file to R2
  - Method: `POST`
  - Path: `/upload`
  - Form-data: `file: <binary>`
  - Response 200:
  ```json
  {
    "success": true,
    "message": "Upload thành công",
    "data": {
      "fileName": "original-name.png",
      "fileUrl": "https://<publicBaseUrl>/<generated>.png",
      "fileSize": 12345
    }
  }
  ```
- Step 2: Persist URL to profile
  - Company logo:
    - Method: `PUT`
    - Path: `/api/recruiter/profile/logo?url=<fileUrl>`
    - Response 200: `RecruiterProfileResponse`
  - Recruiter avatar:
    - Method: `PUT`
    - Path: `/api/recruiter/profile/avatar?url=<fileUrl>`
    - Response 200: `RecruiterProfileResponse`

- Alternative (giống Candidate: upload trực tiếp trong endpoint)
  - Company logo (upload file):
    - Method: `PUT`
    - Path: `/api/recruiter/profile/logo/upload`
    - Form-data: `logo: <binary>`
    - Response 200: `RecruiterProfileResponse`
  - Recruiter avatar (upload file):
    - Method: `PUT`
    - Path: `/api/recruiter/profile/avatar/upload`
    - Form-data: `avatar: <binary>`
    - Response 200: `RecruiterProfileResponse`

---

### Job Postings

#### List my job postings
- Method: `GET`
- Path: `/api/recruiter/jobs`
- Response 200: `JobPostingResponse[]`
```json
[
  {
    "id": 10,
    "title": "Backend Engineer",
    "location": "Hanoi",
    "employmentType": "FULL_TIME",
    "description": "...",
    "requirements": "...",
    "benefits": "...",
    "salaryMin": 1000,
    "salaryMax": 2000,
    "active": true,
    "createdAt": "2025-10-15T07:00:00Z",
    "updatedAt": "2025-10-15T07:00:00Z"
  }
]
```

#### Create a job posting
- Method: `POST`
- Path: `/api/recruiter/jobs`
- Body: `JobPostingRequest`
```json
{
  "title": "Backend Engineer",
  "location": "Hanoi",
  "employmentType": "FULL_TIME",
  "description": "...",
  "requirements": "...",
  "benefits": "...",
  "salaryMin": 1000,
  "salaryMax": 2000,
  "active": true
}
```
- Response 200: `JobPostingResponse`

#### Update a job posting
- Method: `PUT`
- Path: `/api/recruiter/jobs/{id}`
- Body: `JobPostingRequest`
- Response 200: `JobPostingResponse`

#### Delete a job posting
- Method: `DELETE`
- Path: `/api/recruiter/jobs/{id}`
- Response 204: No Content

---

### Job Applications (received for my jobs)

#### List applications for my company
- Method: `GET`
- Path: `/api/recruiter/applications`
- Response 200: `JobApplicationResponse[]`
```json
[
  {
    "id": 100,
    "jobPostingId": 10,
    "candidateUserId": 5,
    "cvUrl": "https://.../cv.pdf",
    "status": "SUBMITTED",
    "note": "",
    "createdAt": "2025-10-15T07:00:00Z",
    "updatedAt": "2025-10-15T07:00:00Z"
  }
]
```

#### Update application status/note
- Method: `PUT`
- Path: `/api/recruiter/applications/{id}`
- Query Params:
  - `status` (required): `SUBMITTED|IN_REVIEW|INTERVIEW|OFFER|REJECTED|WITHDRAWN`
  - `note` (optional)
- Response 200: `JobApplicationResponse`

---

### Data Models

- RecruiterProfileRequest
```json
{
  "companyName": "string (required)",
  "companyWebsite": "string",
  "companyAddress": "string",
  "companySize": "string",
  "about": "string (<=5000)",
  "logoUrl": "string",
  "recruiterName": "string",
  "recruiterTitle": "string",
  "recruiterPhone": "string",
  "recruiterEmail": "string",
  "recruiterLinkedin": "string",
  "recruiterAbout": "string (<=5000)",
  "recruiterAvatarUrl": "string"
}
```

- JobPostingRequest
```json
{
  "title": "string (required)",
  "location": "string",
  "employmentType": "string",
  "description": "string (<=10000)",
  "requirements": "string",
  "benefits": "string",
  "salaryMin": 0,
  "salaryMax": 0,
  "active": true
}
```

---

### Error Codes
- 400: Bad Request (validation, invalid status)
- 401: Unauthorized (missing/invalid token)
- 403: Forbidden (not recruiter/admin, or not owner of resource)
- 404: Not Found (job/application not found)
- 409: Conflict (business rule conflict)


