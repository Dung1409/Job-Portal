# Security – JWT & Spring Security

## JWT Token Config
| Token         | Thời hạn | Lưu ở đâu                  |
|---------------|----------|----------------------------|
| Access Token  | 1 giờ    | Client localStorage        |
| Refresh Token | 7 ngày   | DB bảng `refresh_tokens`   |

- Thuật toán ký: **HS256**
- Secret: minimum 256-bit (32+ ký tự), đọc từ env `JWT_SECRET`
- Header gửi lên: `Authorization: Bearer <accessToken>`

## JWT Flow

### Đăng nhập
```
Client → POST /auth/login { email, password }
  ↓
Server: validate credentials, load UserDetails
  ↓
Tạo accessToken (1h) + refreshToken (7d)
Lưu refreshToken vào bảng refresh_tokens
  ↓
Trả về: { accessToken, refreshToken, user }
```

### Mỗi request có auth
```
Client → GET /api/v1/... + Header: Authorization: Bearer <token>
  ↓
JwtAuthenticationFilter.doFilterInternal()
  → Đọc token từ header
  → JwtTokenProvider.validateToken()  → true/false
  → Nếu valid: parse userId, load UserDetails
  → SecurityContextHolder.setAuthentication(...)
  ↓
Controller chạy bình thường
```

### Refresh token
```
accessToken hết hạn (401 response)
  ↓
Client → POST /auth/refresh { refreshToken }
  ↓
Server: tìm trong DB, kiểm tra expires_at
  → Hợp lệ: tạo accessToken mới (+ rotate refreshToken nếu muốn)
  → Hết hạn / không tồn tại: trả 401, client logout
```

### Đăng xuất
```
Client → POST /auth/logout + Bearer token
  ↓
Server: xóa refreshToken khỏi DB
  ↓
Client: xóa cả 2 token khỏi localStorage
```

## Spring Security Config (tóm tắt)
```java
// Không cần auth
.requestMatchers("/api/v1/auth/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/v1/jobs/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/v1/companies/**").permitAll()

// Theo role
.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.POST, "/api/v1/jobs").hasRole("RECRUITER")
.requestMatchers(HttpMethod.PUT,  "/api/v1/jobs/**").hasRole("RECRUITER")
.requestMatchers(HttpMethod.POST, "/api/v1/applications").hasRole("APPLICANT")

// Còn lại: đăng nhập là đủ
.anyRequest().authenticated()
```

## Validation Rules
| Field         | Rule                                           |
|---------------|------------------------------------------------|
| email         | `@Email`, `@NotBlank`                          |
| password      | `@Size(min=8)`, phải có hoa + thường + số + đặc biệt |
| fullName      | `@NotBlank`, `@Size(min=2, max=100)`           |
| job title     | `@NotBlank`, `@Size(max=300)`                  |
| salaryMin/Max | `@Positive`, min < max                         |
| deadline      | `@Future` (phải là ngày tương lai)             |
| resume file   | Chỉ PDF, tối đa 5MB                            |

## Security Checklist
- [ ] Password luôn bcrypt strength 12 (không bao giờ plain text)
- [ ] Không bao giờ trả Entity User trực tiếp (lộ password hash)
- [ ] JWT secret đủ dài, lấy từ env variable
- [ ] Kiểm tra ownership trước khi sửa/xóa resource
- [ ] File upload: validate type + size trước khi lưu
- [ ] CORS chỉ cho phép origin của frontend
- [ ] Rate limiting (cân nhắc thêm cho endpoint login)