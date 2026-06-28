# User Stories

## Auth (tất cả role)
- [ ] Đăng ký bằng email + password, chọn role Applicant hoặc Recruiter
- [ ] Đăng nhập nhận JWT access token + refresh token
- [ ] Session tự gia hạn qua refresh token (7 ngày)
- [ ] Đăng xuất → refresh token bị vô hiệu hóa
- [ ] Đổi mật khẩu trong trang profile

## Applicant
- [ ] Xem toàn bộ job đang mở mà không cần đăng nhập
- [ ] Tìm kiếm job theo: từ khóa, địa điểm, danh mục, lương, loại hình, kinh nghiệm
- [ ] Xem chi tiết job (mô tả, yêu cầu, phúc lợi, thông tin công ty)
- [ ] Nộp đơn với cover letter + resume PDF (không nộp 2 lần cho 1 job)
- [ ] Xem danh sách đơn đã nộp và trạng thái hiện tại
- [ ] Rút đơn khi trạng thái còn là APPLIED
- [ ] Lưu / bỏ lưu job yêu thích
- [ ] Cập nhật hồ sơ: tiêu đề, tóm tắt, kỹ năng, thông tin cá nhân
- [ ] Upload avatar và resume PDF lên profile

## Recruiter
- [ ] Tạo và quản lý company profile (tên, mô tả, logo, địa chỉ, quy mô)
- [ ] Đăng tin tuyển dụng mới với đầy đủ thông tin
- [ ] Sửa, đóng, xóa tin tuyển dụng của mình
- [ ] Xem danh sách ứng viên đã nộp cho từng tin
- [ ] Xem hồ sơ và resume của ứng viên
- [ ] Cập nhật trạng thái đơn ứng tuyển + ghi chú nội bộ
- [ ] Dashboard: job đang mở, ứng viên mới trong tuần, tổng đơn nhận được

## Admin
- [ ] Dashboard thống kê: tổng user, job, đơn ứng tuyển, công ty
- [ ] Xem danh sách tất cả user, tìm kiếm / lọc
- [ ] Kích hoạt hoặc vô hiệu hóa tài khoản user
- [ ] Xem và xóa bất kỳ job nào vi phạm
- [ ] Xác minh / hủy xác minh công ty
- [ ] Tạo, sửa, xóa danh mục việc làm (categories)