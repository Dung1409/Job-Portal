package com.jobportal.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jobportal.entity.Category;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.enums.ExperienceLevel;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import com.jobportal.enums.Role;
import com.jobportal.repository.CategoryRepository;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;
    private final CategoryRepository categoryRepo;
    private final JobRepository jobRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (jobRepo.count() > 0) return;

        if (categoryRepo.count() == 0) seedCategories();
        if (userRepo.count() == 0) seedUsers();
        seedJobs();
    }

    private void seedCategories() {
        var cats = List.of(
            Category.builder().name("Công nghệ thông tin").icon("💻").isActive(true).build(),
            Category.builder().name("Marketing").icon("📣").isActive(true).build(),
            Category.builder().name("Thiết kế").icon("🎨").isActive(true).build(),
            Category.builder().name("Tài chính / Kế toán").icon("💰").isActive(true).build(),
            Category.builder().name("Kinh doanh / Sales").icon("📊").isActive(true).build(),
            Category.builder().name("Nhân sự").icon("👥").isActive(true).build(),
            Category.builder().name("Vận hành / Logistics").icon("🚚").isActive(true).build()
        );
        categoryRepo.saveAll(cats);
    }

    private User recruiter1, recruiter2;

    private void seedUsers() {
        String pass = passwordEncoder.encode("Abcd@1234");

        var admin = User.builder()
                .email("admin@jobportal.com").password(pass)
                .fullName("Admin System").role(Role.ADMIN).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        recruiter1 = User.builder()
                .email("recruiter@techcorp.com").password(pass)
                .fullName("Nguyen Tuyen Dung").role(Role.RECRUITER).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        recruiter2 = User.builder()
                .email("recruiter2@startup.vn").password(pass)
                .fullName("Tran Nha Tuyen").role(Role.RECRUITER).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        var applicant = User.builder()
                .email("applicant@gmail.com").password(pass)
                .fullName("Le Van Ung Vien").role(Role.APPLICANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        var applicant2 = User.builder()
                .email("applicant2@gmail.com").password(pass)
                .fullName("Pham Thi Tim Viec").role(Role.APPLICANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        userRepo.saveAll(List.of(admin, recruiter1, recruiter2, applicant, applicant2));

        var techCorp = Company.builder()
                .recruiter(recruiter1)
                .name("TechCorp Vietnam")
                .description("Công ty công nghệ hàng đầu Việt Nam, chuyên về giải pháp phần mềm doanh nghiệp.")
                .website("https://techcorp.vn")
                .address("Tầng 10, Tòa nhà Keangnam, Hà Nội")
                .industry("Technology")
                .companySize("51-200")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .build();

        var startupX = Company.builder()
                .recruiter(recruiter2)
                .name("StartupX")
                .description("Startup fintech đang phát triển nhanh, tập trung vào thanh toán di động.")
                .website("https://startupx.vn")
                .address("123 Nguyễn Huệ, Quận 1, TP.HCM")
                .industry("Fintech")
                .companySize("11-50")
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        companyRepo.saveAll(List.of(techCorp, startupX));
    }

    private void seedJobs() {
        var companies = companyRepo.findAll();
        var techCorp = companies.get(0);
        var startupX = companies.get(1);
        var cats = categoryRepo.findByIsActiveTrue();
        var itCat = cats.get(0);
        var designCat = cats.get(2);
        var marketingCat = cats.get(1);

        var jobs = List.of(
            Job.builder().company(techCorp).category(itCat)
                .title("Senior Java Developer")
                .description("Xây dựng và duy trì hệ thống microservices quy mô lớn.")
                .requirements("- Tối thiểu 3 năm kinh nghiệm Java\n- Microservices, REST API\n- Docker, Kubernetes")
                .benefits("- Lương tháng 13\n- Bảo hiểm sức khỏe cao cấp\n- Hybrid 3 ngày/tuần")
                .location("Hà Nội")
                .salaryMin(new java.math.BigDecimal("25000000"))
                .salaryMax(new java.math.BigDecimal("40000000"))
                .currency("VND").jobType(JobType.FULL_TIME)
                .experience(ExperienceLevel.THREE_FIVE_YEARS)
                .status(JobStatus.OPEN).deadline(LocalDate.of(2025, 12, 31))
                .createdAt(LocalDateTime.now()).build(),

            Job.builder().company(techCorp).category(itCat)
                .title("Frontend Developer (React)")
                .description("Phát triển giao diện người dùng cho các sản phẩm SaaS.")
                .requirements("- 1-2 năm kinh nghiệm React\n- HTML, CSS, JavaScript ES6+")
                .location("Hà Nội")
                .salaryMin(new java.math.BigDecimal("15000000"))
                .salaryMax(new java.math.BigDecimal("25000000"))
                .currency("VND").jobType(JobType.FULL_TIME)
                .experience(ExperienceLevel.ONE_TWO_YEARS)
                .status(JobStatus.OPEN).deadline(LocalDate.of(2025, 11, 30))
                .createdAt(LocalDateTime.now()).build(),

            Job.builder().company(techCorp).category(designCat)
                .title("UI/UX Designer")
                .description("Thiết kế trải nghiệm người dùng cho ứng dụng web và mobile.")
                .requirements("- Portfolio thiết kế\n- Thành thạo Figma")
                .location("Remote")
                .salaryMin(new java.math.BigDecimal("12000000"))
                .salaryMax(new java.math.BigDecimal("20000000"))
                .currency("VND").jobType(JobType.REMOTE)
                .experience(ExperienceLevel.ONE_TWO_YEARS)
                .status(JobStatus.OPEN).deadline(LocalDate.of(2025, 12, 15))
                .createdAt(LocalDateTime.now()).build(),

            Job.builder().company(startupX).category(itCat)
                .title("Backend Developer (Node.js)")
                .description("Xây dựng API cho ứng dụng thanh toán di động.")
                .requirements("- 1+ năm Node.js\n- MySQL hoặc PostgreSQL")
                .location("TP. Hồ Chí Minh")
                .salaryMin(new java.math.BigDecimal("18000000"))
                .salaryMax(new java.math.BigDecimal("28000000"))
                .currency("VND").jobType(JobType.FULL_TIME)
                .experience(ExperienceLevel.ONE_TWO_YEARS)
                .status(JobStatus.OPEN).deadline(LocalDate.of(2025, 11, 15))
                .createdAt(LocalDateTime.now()).build(),

            Job.builder().company(startupX).category(marketingCat)
                .title("Digital Marketing Executive")
                .description("Quản lý các kênh marketing số: Facebook Ads, Google Ads, SEO.")
                .requirements("- 1-2 năm Digital Marketing\n- Google/Meta Ads")
                .location("TP. Hồ Chí Minh")
                .salaryMin(new java.math.BigDecimal("10000000"))
                .salaryMax(new java.math.BigDecimal("16000000"))
                .currency("VND").jobType(JobType.FULL_TIME)
                .experience(ExperienceLevel.ONE_TWO_YEARS)
                .status(JobStatus.OPEN).deadline(LocalDate.of(2025, 10, 31))
                .createdAt(LocalDateTime.now()).build()
        );
        jobRepo.saveAll(jobs);
    }
}
