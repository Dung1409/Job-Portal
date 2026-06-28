# Frontend Pages

## Public (không cần đăng nhập)
| File                       | Mô tả                                  |
|----------------------------|----------------------------------------|
| `index.html`               | Landing: hero, featured jobs, categories |
| `pages/jobs.html`          | Danh sách job + search/filter sidebar  |
| `pages/job-detail.html`    | Chi tiết job + nút Apply               |
| `pages/company-detail.html`| Thông tin công ty + job list của họ    |
| `pages/login.html`         | Form đăng nhập                         |
| `pages/register.html`      | Form đăng ký (chọn Applicant/Recruiter)|

## Applicant (`pages/applicant/`)
> Guard: `auth.requireRole('APPLICANT')` đầu mỗi file JS

| File                  | Mô tả                                          |
|-----------------------|------------------------------------------------|
| `dashboard.html`      | Thống kê: đã nộp, đang xem xét, lưu, phỏng vấn |
| `applications.html`   | Danh sách đơn + status badge + timeline        |
| `saved-jobs.html`     | Job đã bookmark                                |
| `profile.html`        | Chỉnh thông tin + upload avatar + resume PDF   |

## Recruiter (`pages/recruiter/`)
> Guard: `auth.requireRole('RECRUITER')` đầu mỗi file JS

| File              | Mô tả                                              |
|-------------------|----------------------------------------------------|
| `dashboard.html`  | Job đang mở, ứng viên mới, tổng đơn nhận           |
| `company.html`    | Chỉnh sửa company profile, upload logo             |
| `jobs.html`       | Bảng danh sách job của tôi + actions               |
| `job-form.html`   | Tạo mới / chỉnh sửa job (dùng chung 1 form)        |
| `applicants.html` | Ứng viên theo job + cập nhật status + xem hồ sơ   |

## Admin (`pages/admin/`)
> Guard: `auth.requireRole('ADMIN')` đầu mỗi file JS

| File              | Mô tả                                         |
|-------------------|-----------------------------------------------|
| `dashboard.html`  | Thống kê hệ thống + biểu đồ                  |
| `users.html`      | Bảng users + filter + toggle active           |
| `companies.html`  | Bảng công ty + nút verify                     |
| `categories.html` | CRUD danh mục (inline table edit)             |
| `jobs.html`       | Tất cả job, có thể xóa job vi phạm           |

## Redirect Logic
```
/ (index)        → Nếu đã đăng nhập → redirect về dashboard theo role
/pages/login     → Nếu đã đăng nhập → redirect về dashboard theo role
/pages/applicant/* → Nếu chưa đăng nhập hoặc sai role → /pages/login
/pages/recruiter/*  → Nếu chưa đăng nhập hoặc sai role → /pages/login
/pages/admin/*      → Nếu chưa đăng nhập hoặc sai role → /pages/login
```

## Dashboard Redirect sau Login
```javascript
function redirectToDashboard(role) {
  const paths = {
    ADMIN:     '/pages/admin/dashboard.html',
    RECRUITER: '/pages/recruiter/dashboard.html',
    APPLICANT: '/pages/applicant/dashboard.html',
  };
  window.location.href = paths[role] || '/index.html';
}
```