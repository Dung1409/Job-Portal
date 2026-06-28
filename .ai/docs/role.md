# Roles

## 🛡️ Admin
- Quản lý toàn bộ hệ thống
- Xem thống kê: tổng user, job, ứng tuyển, công ty
- Kích hoạt / vô hiệu hóa tài khoản bất kỳ
- Xác minh (verify) công ty
- CRUD danh mục việc làm (categories)
- Xóa job vi phạm nội dung

## 💼 Recruiter
- Mỗi recruiter gắn với **1 company profile**
- Đăng, chỉnh sửa, đóng / xóa tin tuyển dụng
- Xem danh sách ứng viên cho từng tin (chỉ tin của mình)
- Cập nhật trạng thái đơn ứng tuyển + ghi chú nội bộ
- Dashboard: job đang mở, ứng viên mới tuần này

## 👤 Applicant
- Xem job không cần đăng nhập
- Đăng nhập để: nộp đơn, lưu job, quản lý hồ sơ
- Upload resume PDF lên profile (dùng mặc định khi nộp đơn)
- Theo dõi trạng thái từng đơn đã nộp
- Rút đơn khi trạng thái còn là APPLIED

## Access Control Matrix

| Feature                   | Public | Applicant | Recruiter  | Admin |
|---------------------------|--------|-----------|------------|-------|
| Xem / tìm việc làm        | ✅     | ✅        | ✅         | ✅    |
| Xem chi tiết công ty      | ✅     | ✅        | ✅         | ✅    |
| Nộp đơn                   | ❌     | ✅        | ❌         | ❌    |
| Lưu job                   | ❌     | ✅        | ❌         | ❌    |
| Sửa hồ sơ cá nhân        | ❌     | ✅        | ✅         | ✅    |
| Đăng / sửa / xóa job     | ❌     | ❌        | ✅ (own)   | ✅    |
| Xem ứng viên của job      | ❌     | ❌        | ✅ (own)   | ✅    |
| Cập nhật trạng thái đơn  | ❌     | ❌        | ✅ (own)   | ✅    |
| Quản lý company profile   | ❌     | ❌        | ✅ (own)   | ✅    |
| Quản lý categories        | ❌     | ❌        | ❌         | ✅    |
| Quản lý tất cả users      | ❌     | ❌        | ❌         | ✅    |
| Xem thống kê hệ thống     | ❌     | ❌        | ❌         | ✅    |
| Verify công ty            | ❌     | ❌        | ❌         | ✅    |

## Application Status Lifecycle
```
                    [Recruiter cập nhật]
APPLIED ──────► REVIEWING ──────► INTERVIEWED ──────► OFFERED
   │                │                  │
   │                └──────────────────┴────────────► REJECTED
   │
   └── Applicant có thể WITHDRAW khi status = APPLIED
```

| Status      | Ai thay đổi | Ý nghĩa                           |
|-------------|-------------|-----------------------------------|
| APPLIED     | System      | Ứng viên vừa nộp đơn              |
| REVIEWING   | Recruiter   | Đang xem xét hồ sơ               |
| INTERVIEWED | Recruiter   | Đã / đang phỏng vấn              |
| OFFERED     | Recruiter   | Đề xuất nhận việc                 |
| REJECTED    | Recruiter   | Từ chối                           |