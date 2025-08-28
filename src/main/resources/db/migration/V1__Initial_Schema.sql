-- Esquema inicial alineado con las entidades JPA (User, Job, Application)

-- Tabla de usuarios (User)
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  role VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de ofertas (Job)
CREATE TABLE jobs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description LONGTEXT NOT NULL,
  location VARCHAR(255),
  posted_date DATETIME(6) NOT NULL,
  recruiter_id BIGINT NOT NULL,
  CONSTRAINT fk_jobs_recruiter FOREIGN KEY (recruiter_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de aplicaciones (Application)
CREATE TABLE applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  applied_date DATETIME(6) NOT NULL,
  status VARCHAR(50) NOT NULL,
  candidate_id BIGINT NOT NULL,
  job_id BIGINT NOT NULL,
  CONSTRAINT fk_applications_candidate FOREIGN KEY (candidate_id) REFERENCES users(id) ON DELETE RESTRICT,
  CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;