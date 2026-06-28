# API Endpoints

## Base URL
```
http://localhost:8080/api/v1
```

## Response Format
```json
// Thành công
{ "success": true, "message": "...", "data": { ... }, "timestamp": "2025-01-01T10:00:00Z" }

// Phân trang
{ "success": true, "data": { "content": [...], "page": 0, "size": 10, "totalElements": 234, "totalPages": 24 } }

// Lỗi
{ "success": false, "message": "Không tìm thấy job", "error": "NOT_FOUND", "status": 404 }
```

---

## /auth – Xác thực

| Method | Path            | Auth   | Mô tả                          |
|--------|-----------------|--------|--------------------------------|
| POST   | /auth/register  | Public | Đăng ký tài khoản mới          |
| POST   | /auth/login     | Public | Đăng nhập, nhận JWT            |
| POST   | /auth/refresh   | Public | Làm mới access token           |
| POST   | /auth/logout    | ✅     | Vô hiệu refresh token          |
| GET    | /auth/me        | ✅     | Thông tin user hiện tại        |
| PUT    | /auth/password  | ✅     | Đổi mật khẩu                   |

**POST /auth/register**
```json
// Request
{ "email": "user@gmail.com", "password": "Abcd@1234", "fullName": "Nguyen Van A", "role": "APPLICANT" }
```

**POST /auth/login**
```json
// Request
{ "email": "user@gmail.com", "password": "Abcd@1234" }

// Response
{
  "accessToken": "eyJ...", "refreshToken": "eyJ...",
  "tokenType": "Bearer", "expiresIn": 3600,
  "user": { "id": 1, "email": "...", "fullName": "...", "role": "APPLICANT" }
}
```

---

## /jobs – Việc làm

| Method | Path                | Auth           | Mô tả                        |
|--------|---------------------|----------------|------------------------------|
| GET    | /jobs               | Public         | Tìm kiếm, lọc job (paginated)|
| GET    | /jobs/{id}          | Public         | Chi tiết job                 |
| GET    | /jobs/my            | RECRUITER      | Job của tôi                  |
| POST   | /jobs               | RECRUITER      | Đăng tin mới                 |
| PUT    | /jobs/{id}          | RECRUITER(own) | Cập nhật tin                 |
| DELETE | /jobs/{id}          | RECRUITER/ADMIN| Xóa tin                      |
| PATCH  | /jobs/{id}/status   | RECRUITER/ADMIN| Đổi trạng thái               |

**GET /jobs – Query Params**
```
keyword=java&location=hanoi&categoryId=1
&jobType=FULL_TIME&experience=1_2_YEARS
&salaryMin=10000000&salaryMax=30000000
&page=0&size=10&sort=createdAt,desc
```

**POST /jobs – Request Body**
```json
{
  "title": "Senior Java Developer",
  "categoryId": 1,
  "description": "Mô tả công việc...",
  "requirements": "Yêu cầu...",
  "benefits": "Phúc lợi...",
  "location": "Hà Nội",
  "salaryMin": 20000000,
  "salaryMax": 35000000,
  "currency": "VND",
  "jobType": "FULL_TIME",
  "experience": "3_5_YEARS",
  "deadline": "2025-12-31"
}
```

---

## /applications – Ứng tuyển

| Method | Path                        | Auth       | Mô tả                         |
|--------|-----------------------------|------------|-------------------------------|
| POST   | /applications               | APPLICANT  | Nộp đơn (multipart/form-data) |
| GET    | /applications/my            | APPLICANT  | Đơn của tôi                   |
| DELETE | /applications/{id}          | APPLICANT  | Rút đơn                       |
| GET    | /applications/job/{jobId}   | RECRUITER  | Ứng viên của một job          |
| GET    | /applications/{id}          | ✅         | Chi tiết đơn                  |
| PATCH  | /applications/{id}/status   | RECRUITER  | Cập nhật trạng thái + note    |

**POST /applications (multipart/form-data)**
```
jobId:        1
coverLetter:  "Dear Hiring Manager..."
resume:       [file .pdf]   ← optional, dùng profile resume nếu bỏ trống
```

**PATCH /applications/{id}/status**
```json
{ "status": "INTERVIEWED", "note": "Lịch phỏng vấn: Thứ 2, 9:00 AM" }
```

---

## /companies – Công ty

| Method | Path                      | Auth              | Mô tả               |
|--------|---------------------------|-------------------|---------------------|
| GET    | /companies                | Public            | Danh sách công ty   |
| GET    | /companies/{id}           | Public            | Chi tiết + job list |
| POST   | /companies                | RECRUITER         | Tạo company profile |
| PUT    | /companies/{id}           | RECRUITER(own)    | Cập nhật            |
| PATCH  | /companies/{id}/verify    | ADMIN             | Xác minh công ty    |

---

## /categories – Danh mục

| Method | Path              | Auth   | Mô tả           |
|--------|-------------------|--------|-----------------|
| GET    | /categories       | Public | Tất cả danh mục |
| POST   | /categories       | ADMIN  | Tạo mới         |
| PUT    | /categories/{id}  | ADMIN  | Cập nhật        |
| DELETE | /categories/{id}  | ADMIN  | Xóa             |

---

## /profile – Hồ sơ cá nhân

| Method | Path             | Auth      | Mô tả                   |
|--------|------------------|-----------|-------------------------|
| GET    | /profile         | ✅        | Xem hồ sơ của tôi       |
| PUT    | /profile         | ✅        | Cập nhật hồ sơ          |
| POST   | /profile/avatar  | ✅        | Upload avatar (image)   |
| POST   | /profile/resume  | APPLICANT | Upload resume (PDF)     |

---

## /saved-jobs – Job đã lưu

| Method | Path                  | Auth      | Mô tả          |
|--------|-----------------------|-----------|----------------|
| GET    | /saved-jobs           | APPLICANT | Danh sách đã lưu |
| POST   | /saved-jobs/{jobId}   | APPLICANT | Lưu job        |
| DELETE | /saved-jobs/{jobId}   | APPLICANT | Bỏ lưu job     |

---

## /admin – Quản trị

| Method | Path                    | Auth  | Mô tả                         |
|--------|-------------------------|-------|-------------------------------|
| GET    | /admin/stats            | ADMIN | Thống kê hệ thống             |
| GET    | /admin/users            | ADMIN | Tất cả users (có filter)      |
| PATCH  | /users/{id}/active      | ADMIN | Bật/tắt tài khoản             |
| GET    | /admin/jobs             | ADMIN | Tất cả jobs                   |
| GET    | /admin/applications     | ADMIN | Tất cả đơn ứng tuyển          |

**GET /admin/stats – Response**
```json
{
  "totalUsers": 1250, "totalRecruiters": 120, "totalApplicants": 1100,
  "totalJobs": 480,   "openJobs": 310,
  "totalApplications": 3200, "totalCompanies": 115,
  "newUsersThisMonth": 85
}
```

---

## HTTP Status Codes
| Code | Ý nghĩa                          |
|------|----------------------------------|
| 200  | OK                               |
| 201  | Created (tạo mới thành công)     |
| 204  | No Content (xóa thành công)      |
| 400  | Bad Request / Validation error   |
| 401  | Unauthorized (chưa đăng nhập)    |
| 403  | Forbidden (không đủ quyền)       |
| 404  | Not Found                        |
| 409  | Conflict (đã tồn tại)            |
| 500  | Internal Server Error            |