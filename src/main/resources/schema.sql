-- =============================================
-- Tabla: Company
-- Almacena la información de las empresas.
-- =============================================
CREATE TABLE `Company` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    email VARCHAR(255) UNIQUE,
    website VARCHAR(255),
    phone VARCHAR(20)
);

-- =============================================
-- Tabla: User
-- Almacena los datos de todos los usuarios (candidatos, reclutadores, administradores).
-- =============================================
CREATE TABLE `User` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role ENUM('admin', 'candidate', 'recruiter') NOT NULL,
    resume_file VARCHAR(255),
    skills TEXT,
    experience TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- Tabla: JobOffer
-- Contiene los detalles de las ofertas de trabajo publicadas.
-- =============================================
CREATE TABLE `JobOffer` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255),
    salary VARCHAR(100),
    employment_type ENUM('full-time', 'part-time', 'contract'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    recruiter_id BIGINT,
    company_id INT,
    FOREIGN KEY (recruiter_id) REFERENCES `User`(id) ON DELETE SET NULL,
    FOREIGN KEY (company_id) REFERENCES `Company`(id) ON DELETE CASCADE
);

-- =============================================
-- Tabla: Application
-- Registra las postulaciones de los candidatos a las ofertas de trabajo.
-- =============================================
CREATE TABLE `Application` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status ENUM('applied', 'preselected', 'interview', 'rejected', 'hired') DEFAULT 'applied',
    messages TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    candidate_id BIGINT NOT NULL,
    job_offer_id INT NOT NULL,
    FOREIGN KEY (candidate_id) REFERENCES `User`(id) ON DELETE CASCADE,
    FOREIGN KEY (job_offer_id) REFERENCES `JobOffer`(id) ON DELETE CASCADE
);

-- =============================================
-- Tabla: UserCompany (Tabla de Unión)
-- Relaciona a los usuarios con las empresas y define su rol/posición.
-- =============================================
CREATE TABLE `UserCompany` (
    user_id BIGINT NOT NULL,
    company_id INT NOT NULL,
    position VARCHAR(255),
    startDate DATE,
    endDate DATE,
    isCurrentlyEmployed BOOLEAN,
    relationshipType VARCHAR(100),
    PRIMARY KEY (user_id, company_id),
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES `Company`(id) ON DELETE CASCADE
);

-- =============================================
-- Tabla: AdminLog
-- Guarda un registro de las acciones importantes realizadas por los administradores.
-- =============================================
CREATE TABLE `AdminLog` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    target_type ENUM('user', 'job_offer', 'application'),
    target_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE NO ACTION
);

-- =============================================
-- Tabla: Notification
-- Almacena las notificaciones para los usuarios.
-- =============================================
CREATE TABLE `Notification` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('registration', 'application_status', 'job_offer'),
    message TEXT NOT NULL,
    `read` BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE
);
