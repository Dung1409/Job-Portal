# Backend Architecture – Spring Boot

## Package Structure
```
com.jobportal/
│
├── config/
│   ├── SecurityConfig.java        -- Spring Security filter chain, CORS, role rules
│   ├── CorsConfig.java            -- Cho phép frontend origin
│   ├── OpenApiConfig.java         -- Swagger UI + JWT bearer auth
│   └── AppConfig.java             -- Bean: BCryptPasswordEncoder, ModelMapper
│
├── security/
│   ├── JwtTokenProvider.java      -- Tạo / validate / parse JWT
│   ├── JwtAuthenticationFilter.java -- OncePerRequestFilter: đọc token từ header
│   ├── UserDetailsServiceImpl.java  -- loadUserByUsername
│   └── CustomUserDetails.java       -- Wrap User entity, implements UserDetails
│
├── entity/                        -- JPA Entities (ánh xạ với bảng DB)
│   ├── User.java
│   ├── Company.java
│   ├── Job.java
│   ├── Category.java
│   ├── Application.java
│   ├── ApplicantProfile.java
│   ├── SavedJob.java
│   └── RefreshToken.java
│
├── enums/
│   ├── Role.java                  -- ADMIN, RECRUITER, APPLICANT
│   ├── JobType.java               -- FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE
│   ├── JobStatus.java             -- DRAFT, OPEN, CLOSED, EXPIRED
│   ├── ExperienceLevel.java
│   └── ApplicationStatus.java    -- APPLIED, REVIEWING, INTERVIEWED, OFFERED, REJECTED
│
├── repository/                    -- extends JpaRepository<Entity, Long>
│   ├── UserRepository.java
│   ├── CompanyRepository.java
│   ├── JobRepository.java         -- @Query custom cho search/filter
│   ├── CategoryRepository.java
│   ├── ApplicationRepository.java
│   ├── ApplicantProfileRepository.java
│   ├── SavedJobRepository.java
│   └── RefreshTokenRepository.java
│
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── CreateJobRequest.java
│   │   ├── UpdateJobRequest.java
│   │   ├── ApplyJobRequest.java
│   │   ├── UpdateApplicationStatusRequest.java
│   │   └── UpdateProfileRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── JobResponse.java
│       ├── JobSummaryResponse.java
│       ├── ApplicationResponse.java
│       ├── CompanyResponse.java
│       ├── UserResponse.java
│       ├── PageResponse.java
│       └── ApiResponse.java       -- Generic wrapper T
│
├── service/
│   ├── AuthService.java / AuthServiceImpl.java
│   ├── JobService.java / JobServiceImpl.java
│   ├── ApplicationService.java / ApplicationServiceImpl.java
│   ├── CompanyService.java / CompanyServiceImpl.java
│   ├── CategoryService.java / CategoryServiceImpl.java
│   ├── UserService.java / UserServiceImpl.java
│   ├── ProfileService.java / ProfileServiceImpl.java
│   ├── SavedJobService.java / SavedJobServiceImpl.java
│   ├── AdminService.java / AdminServiceImpl.java
│   ├── FileStorageService.java    -- Upload file local (có thể swap S3)
│   ├── EmailService.java          -- JavaMailSender thông báo trạng thái
│   └── RefreshTokenService.java
│
├── controller/
│   ├── AuthController.java        -- @RequestMapping("/api/v1/auth")
│   ├── JobController.java         -- @RequestMapping("/api/v1/jobs")
│   ├── ApplicationController.java -- @RequestMapping("/api/v1/applications")
│   ├── CompanyController.java     -- @RequestMapping("/api/v1/companies")
│   ├── CategoryController.java    -- @RequestMapping("/api/v1/categories")
│   ├── ProfileController.java     -- @RequestMapping("/api/v1/profile")
│   ├── SavedJobController.java    -- @RequestMapping("/api/v1/saved-jobs")
│   └── AdminController.java       -- @RequestMapping("/api/v1/admin")
│
└── exception/
    ├── GlobalExceptionHandler.java     -- @ControllerAdvice, bắt tất cả exception
    ├── ResourceNotFoundException.java  -- 404
    ├── UnauthorizedException.java      -- 403
    ├── BadRequestException.java        -- 400
    └── DuplicateResourceException.java -- 409
```

## Coding Rules

### Controller
- Chỉ nhận input, gọi service, trả response
- KHÔNG chứa business logic
- Luôn trả `ResponseEntity<ApiResponse<T>>`

### Service
- Toàn bộ business logic ở đây
- `@Transactional` trên mọi write method
- Kiểm tra ownership trước khi sửa/xóa resource của người khác

### Repository
- Query phức tạp dùng `@Query` hoặc `Specification`
- KHÔNG gọi repository từ Controller

### DTO
- Input: `*Request` – annotate validation (`@NotBlank`, `@Email`, `@Positive`...)
- Output: `*Response` – KHÔNG bao giờ trả trực tiếp Entity (lộ password)
- Dùng ModelMapper hoặc map thủ công trong Service

### Ownership Check Pattern
```java
Job job = jobRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

Long currentUserId = ((CustomUserDetails) auth.getPrincipal()).getId();
if (!job.getCompany().getRecruiter().getId().equals(currentUserId)) {
    throw new UnauthorizedException("Bạn không có quyền chỉnh sửa job này");
}
```

## Dependencies (pom.xml)
```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-validation
spring-boot-starter-mail
io.jsonwebtoken:jjwt-api:0.12.3
io.jsonwebtoken:jjwt-impl:0.12.3
io.jsonwebtoken:jjwt-jackson:0.12.3
com.mysql:mysql-connector-j
org.projectlombok:lombok
org.modelmapper:modelmapper:3.1.1
org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0
```

## application.yml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate        # update khi dev, validate khi prod
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

app:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-minimum-32-chars!!}
    access-token-expiry-ms: 3600000      # 1 giờ
    refresh-token-expiry-days: 7

  upload:
    dir: ./uploads
    max-file-size: 5MB
    allowed-types: application/pdf,image/jpeg,image/png

  cors:
    allowed-origins: http://localhost:3000,http://localhost:5500
```