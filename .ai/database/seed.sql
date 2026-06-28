-- ============================================================
-- Job Portal – Seed Data (dev/test)
-- Password cho tất cả: Password@123 (bcrypt)
-- ============================================================
USE job_portal;

-- ------------------------------------------------------------
-- Users
-- ------------------------------------------------------------
INSERT INTO users (id, email, password, full_name, role, is_active) VALUES
(1, 'admin@jobportal.com',      '$2a$12$...bcryptHash...', 'Admin System',         'ADMIN',     1),
(2, 'recruiter@techcorp.com',   '$2a$12$...bcryptHash...', 'Nguyen Tuyen Dung',    'RECRUITER', 1),
(3, 'recruiter2@startup.vn',    '$2a$12$...bcryptHash...', 'Tran Nha Tuyen',       'RECRUITER', 1),
(4, 'applicant@gmail.com',      '$2a$12$...bcryptHash...', 'Le Van Ung Vien',      'APPLICANT', 1),
(5, 'applicant2@gmail.com',     '$2a$12$...bcryptHash...', 'Pham Thi Tim Viec',    'APPLICANT', 1);

-- ------------------------------------------------------------
-- Categories
-- ------------------------------------------------------------
INSERT INTO categories (id, name, icon, is_active) VALUES
(1, 'Công nghệ thông tin', '💻', 1),
(2, 'Marketing',           '📣', 1),
(3, 'Thiết kế',            '🎨', 1),
(4, 'Tài chính / Kế toán', '💰', 1),
(5, 'Kinh doanh / Sales',  '📊', 1),
(6, 'Nhân sự',             '👥', 1),
(7, 'Vận hành / Logistics','🚚', 1);

-- ------------------------------------------------------------
-- Companies
-- ------------------------------------------------------------
INSERT INTO companies (id, recruiter_id, name, description, website, address, industry, size, is_verified) VALUES
(1, 2, 'TechCorp Vietnam',
   'Công ty công nghệ hàng đầu Việt Nam, chuyên về giải pháp phần mềm doanh nghiệp.',
   'https://techcorp.vn', 'Tầng 10, Tòa nhà Keangnam, Hà Nội', 'Technology', '51-200', 1),
(2, 3, 'StartupX',
   'Startup fintech đang phát triển nhanh, tập trung vào thanh toán di động.',
   'https://startupx.vn', '123 Nguyễn Huệ, Quận 1, TP.HCM', 'Fintech', '11-50', 0);

-- ------------------------------------------------------------
-- Jobs
-- ------------------------------------------------------------
INSERT INTO jobs (company_id, category_id, title, description, requirements, benefits, location, salary_min, salary_max, job_type, experience, status, deadline) VALUES
(1, 1, 'Senior Java Developer',
 'Xây dựng và duy trì hệ thống microservices quy mô lớn. Làm việc với Spring Boot, Kafka, Docker.',
 '- Tối thiểu 3 năm kinh nghiệm Java\n- Hiểu biết về microservices, REST API\n- Biết Docker, Kubernetes là lợi thế',
 '- Lương tháng 13\n- Bảo hiểm sức khỏe cao cấp\n- Làm việc hybrid 3 ngày/tuần',
 'Hà Nội', 25000000, 40000000, 'FULL_TIME', '3_5_YEARS', 'OPEN', '2025-12-31'),

(1, 1, 'Frontend Developer (React)',
 'Phát triển giao diện người dùng cho các sản phẩm SaaS. Làm việc chặt chẽ với UI/UX designer.',
 '- 1-2 năm kinh nghiệm React\n- Thành thạo HTML, CSS, JavaScript ES6+\n- Biết TypeScript là lợi thế',
 '- Môi trường trẻ, năng động\n- Đào tạo kỹ thuật thường xuyên\n- Flexible working hours',
 'Hà Nội', 15000000, 25000000, 'FULL_TIME', '1_2_YEARS', 'OPEN', '2025-11-30'),

(1, 3, 'UI/UX Designer',
 'Thiết kế trải nghiệm người dùng cho ứng dụng web và mobile. Làm việc với Figma.',
 '- Portfolio thể hiện kỹ năng thiết kế\n- Thành thạo Figma\n- Tư duy user-centered design',
 '- Ngân sách mua công cụ thiết kế\n- Remote hoàn toàn\n- Review lương 6 tháng/lần',
 'Remote', 12000000, 20000000, 'REMOTE', '1_2_YEARS', 'OPEN', '2025-12-15'),

(2, 1, 'Backend Developer (Node.js)',
 'Xây dựng API cho ứng dụng thanh toán di động. Yêu cầu hiểu biết về bảo mật.',
 '- 1+ năm kinh nghiệm Node.js\n- Biết MySQL hoặc PostgreSQL\n- Quan tâm đến bảo mật ứng dụng',
 '- Stock options\n- Lunch allowance\n- MacBook Pro',
 'TP. Hồ Chí Minh', 18000000, 28000000, 'FULL_TIME', '1_2_YEARS', 'OPEN', '2025-11-15'),

(2, 2, 'Digital Marketing Executive',
 'Quản lý các kênh marketing số: Facebook Ads, Google Ads, SEO, Email Marketing.',
 '- 1-2 năm kinh nghiệm Digital Marketing\n- Có chứng chỉ Google/Meta Ads là lợi thế',
 '- KPI bonus hấp dẫn\n- Budget chạy ads lớn để thực hành\n- Team trẻ sáng tạo',
 'TP. Hồ Chí Minh', 10000000, 16000000, 'FULL_TIME', '1_2_YEARS', 'OPEN', '2025-10-31');

-- ------------------------------------------------------------
-- Applicant Profiles
-- ------------------------------------------------------------
INSERT INTO applicant_profiles (user_id, headline, summary, skills, address) VALUES
(4, 'Java Backend Developer | 2 năm kinh nghiệm',
 'Lập trình viên backend với kinh nghiệm xây dựng RESTful API và hệ thống vi dịch vụ.',
 '["Java","Spring Boot","MySQL","Docker","Git"]',
 'Hà Nội'),
(5, 'Marketing Executive | Content & Ads',
 'Chuyên viên marketing với thế mạnh về nội dung và quảng cáo trực tuyến.',
 '["Facebook Ads","Google Ads","SEO","Canva","Excel"]',
 'TP. Hồ Chí Minh');

-- ------------------------------------------------------------
-- Sample Applications
-- ------------------------------------------------------------
INSERT INTO applications (job_id, applicant_id, cover_letter, status) VALUES
(1, 4, 'Tôi rất quan tâm đến vị trí Senior Java Developer tại TechCorp...', 'REVIEWING'),
(2, 4, 'Với kinh nghiệm 2 năm về backend, tôi muốn mở rộng sang frontend...', 'APPLIED'),
(5, 5, 'Tôi có 1.5 năm kinh nghiệm chạy Facebook và Google Ads...', 'APPLIED');

-- ------------------------------------------------------------
-- Saved Jobs
-- ------------------------------------------------------------
INSERT INTO saved_jobs (user_id, job_id) VALUES
(4, 3),
(4, 4),
(5, 5);