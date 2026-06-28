# Project Overview

## Mô tả
Web application kết nối **Nhà tuyển dụng** và **Ứng viên**, quản lý bởi **Admin**.

## Tech Stack
| Layer    | Technology                              |
|----------|-----------------------------------------|
| Frontend | HTML5, CSS3, Vanilla JavaScript (ES6+)  |
| Backend  | Java 17 + Spring Boot 3.x              |
| Database | MySQL 8.x                               |
| Auth     | JWT (access token 1h + refresh token 7d)|
| Build    | Maven                                   |
| API Docs | Swagger / SpringDoc OpenAPI 3           |

## Tính năng chính
- Đăng ký / đăng nhập theo role (Admin / Recruiter / Applicant)
- Tìm kiếm việc làm: keyword, địa điểm, danh mục, lương, loại hình
- Nộp đơn kèm cover letter + resume (PDF)
- Theo dõi trạng thái đơn ứng tuyển
- Quản lý hồ sơ cá nhân + upload CV
- Recruiter đăng tin, xem danh sách ứng viên, cập nhật trạng thái
- Admin quản lý user, công ty, danh mục, thống kê hệ thống

## Cấu trúc thư mục project
```
job-portal/
├── .ai/                        ← AI context (repo này)
├── frontend/
│   ├── index.html
│   ├── pages/
│   │   ├── admin/
│   │   ├── recruiter/
│   │   └── applicant/
│   └── assets/
│       ├── css/
│       └── js/
│           ├── core/           ← api.js, auth.js, utils.js
│           ├── components/     ← navbar.js, job-card.js, pagination.js
│           └── pages/          ← logic từng page
└── backend/
    └── src/main/java/com/jobportal/
        ├── config/
        ├── security/
        ├── entity/
        ├── enums/
        ├── repository/
        ├── dto/
        ├── service/
        ├── controller/
        └── exception/
```

## URLs (local dev)
| Service    | URL                                     |
|------------|-----------------------------------------|
| Backend    | http://localhost:8080                   |
| API        | http://localhost:8080/api/v1            |
| Swagger UI | http://localhost:8080/swagger-ui.html   |
| Frontend   | http://localhost:5500                   |