# .ai – Job Portal Project Context

AI context folder cho project **Job Portal**.
Đọc file này trước, sau đó vào từng folder theo task đang làm.

## Folder Structure

```
.ai/
├── README.md                  ← START HERE
│
├── docs/                      ← Tổng quan dự án
│   ├── overview.md            -- Tech stack, mô tả project
│   ├── roles.md               -- 3 role: Admin / Recruiter / Applicant
│   └── user-stories.md        -- User stories theo từng role
│
├── database/                  ← Mọi thứ liên quan DB
│   ├── schema.sql             -- Full CREATE TABLE statements
│   ├── schema.md              -- ER diagram + giải thích từng bảng
│   └── seed.sql               -- Dữ liệu mẫu để dev/test
│
├── backend/                   ← Spring Boot
│   ├── architecture.md        -- Package structure, conventions
│   ├── security.md            -- JWT flow, Spring Security config
│   └── api.md                 -- Tất cả REST endpoints
│
├── frontend/                  ← HTML/CSS/JS
│   ├── pages.md               -- Danh sách pages theo role
│   ├── architecture.md        -- JS modules, CSS structure
│   └── components.md          -- api.js, auth.js, reusable components
│
└── planning/                  ← Kế hoạch & tiến độ
    ├── sprint.md              -- Task breakdown theo phase
    └── conventions.md         -- Git, naming, coding standards
```

## Tech Stack (quick ref)
| Layer    | Tech                           |
|----------|--------------------------------|
| Frontend | HTML5 · CSS3 · Vanilla JS ES6+ |
| Backend  | Java 17 · Spring Boot 3.x      |
| Database | MySQL 8.x                      |
| Auth     | JWT (access 1h + refresh 7d)   |
| Build    | Maven                          |

## Roles
- **Admin** – quản lý toàn hệ thống
- **Recruiter** – đăng tin, xem ứng viên
- **Applicant** – tìm việc, nộp đơn

## Khi làm task, đọc file nào?

| Task                  | Đọc file                              |
|-----------------------|---------------------------------------|
| Thiết kế DB / Entity  | `database/schema.md` + `schema.sql`   |
| Viết API / Controller | `backend/api.md` + `backend/architecture.md` |
| JWT / Security        | `backend/security.md`                 |
| Viết Frontend page    | `frontend/pages.md` + `frontend/components.md` |
| Biết role được làm gì | `docs/roles.md` + `docs/user-stories.md` |
| Xem tiến độ / task    | `planning/sprint.md`                  |
| Hỏi naming / git      | `planning/conventions.md`             |