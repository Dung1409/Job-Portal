-- ============================================================
-- Job Portal – MySQL Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS job_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE job_portal;

-- ------------------------------------------------------------
-- 1. users
-- ------------------------------------------------------------
CREATE TABLE users (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  email      VARCHAR(255)  NOT NULL UNIQUE,
  password   VARCHAR(255)  NOT NULL,           -- bcrypt hashed
  full_name  VARCHAR(100)  NOT NULL,
  phone      VARCHAR(20),
  avatar_url VARCHAR(500),
  role       ENUM('ADMIN','RECRUITER','APPLICANT') NOT NULL DEFAULT 'APPLICANT',
  is_active  TINYINT(1)    NOT NULL DEFAULT 1,
  created_at DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 2. companies
-- ------------------------------------------------------------
CREATE TABLE companies (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  recruiter_id BIGINT        NOT NULL,
  name         VARCHAR(200)  NOT NULL,
  description  TEXT,
  website      VARCHAR(300),
  logo_url     VARCHAR(500),
  address      VARCHAR(500),
  industry     VARCHAR(100),
  size         ENUM('1-10','11-50','51-200','201-500','500+'),
  is_verified  TINYINT(1)    DEFAULT 0,
  created_at   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (recruiter_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 3. categories
-- ------------------------------------------------------------
CREATE TABLE categories (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(100) NOT NULL UNIQUE,
  icon      VARCHAR(100),
  is_active TINYINT(1)   DEFAULT 1
);

-- ------------------------------------------------------------
-- 4. jobs
-- ------------------------------------------------------------
CREATE TABLE jobs (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  company_id   BIGINT       NOT NULL,
  category_id  BIGINT,
  title        VARCHAR(300) NOT NULL,
  description  TEXT         NOT NULL,
  requirements TEXT,
  benefits     TEXT,
  location     VARCHAR(200),
  salary_min   DECIMAL(15,2),
  salary_max   DECIMAL(15,2),
  currency     VARCHAR(10)  DEFAULT 'VND',
  job_type     ENUM('FULL_TIME','PART_TIME','CONTRACT','INTERNSHIP','REMOTE'),
  experience   ENUM('NO_EXPERIENCE','UNDER_1_YEAR','1_2_YEARS','3_5_YEARS','OVER_5_YEARS'),
  status       ENUM('DRAFT','OPEN','CLOSED','EXPIRED') DEFAULT 'OPEN',
  deadline     DATE,
  views        INT          DEFAULT 0,
  created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (company_id)  REFERENCES companies(id)  ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 5. applicant_profiles
-- ------------------------------------------------------------
CREATE TABLE applicant_profiles (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id       BIGINT        NOT NULL UNIQUE,
  headline      VARCHAR(300),
  summary       TEXT,
  resume_url    VARCHAR(500),
  linkedin_url  VARCHAR(300),
  github_url    VARCHAR(300),
  skills        TEXT,                          -- JSON array: ["Java","Spring Boot"]
  date_of_birth DATE,
  gender        ENUM('MALE','FEMALE','OTHER'),
  address       VARCHAR(500),
  created_at    DATETIME      DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 6. applications
-- ------------------------------------------------------------
CREATE TABLE applications (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_id       BIGINT NOT NULL,
  applicant_id BIGINT NOT NULL,
  cover_letter TEXT,
  resume_url   VARCHAR(500),                  -- snapshot tại thời điểm nộp
  status       ENUM('APPLIED','REVIEWING','INTERVIEWED','OFFERED','REJECTED') DEFAULT 'APPLIED',
  note         TEXT,                          -- ghi chú nội bộ của recruiter
  applied_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_job_applicant (job_id, applicant_id),
  FOREIGN KEY (job_id)       REFERENCES jobs(id)  ON DELETE CASCADE,
  FOREIGN KEY (applicant_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 7. saved_jobs
-- ------------------------------------------------------------
CREATE TABLE saved_jobs (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id  BIGINT NOT NULL,
  job_id   BIGINT NOT NULL,
  saved_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_saved (user_id, job_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (job_id)  REFERENCES jobs(id)  ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 8. refresh_tokens
-- ------------------------------------------------------------
CREATE TABLE refresh_tokens (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT        NOT NULL,
  token      VARCHAR(500)  NOT NULL UNIQUE,
  expires_at DATETIME      NOT NULL,
  created_at DATETIME      DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Indexes
-- ------------------------------------------------------------
CREATE INDEX idx_jobs_status     ON jobs(status);
CREATE INDEX idx_jobs_category   ON jobs(category_id);
CREATE INDEX idx_jobs_deadline   ON jobs(deadline);
CREATE INDEX idx_apps_applicant  ON applications(applicant_id);
CREATE INDEX idx_apps_job        ON applications(job_id);
CREATE INDEX idx_apps_status     ON applications(status);