# 🏗️ Labour-Net

**A comprehensive Labour Management Platform connecting Workers, Agencies, and Administrators**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Test Credentials](#test-credentials)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Security Features](#security-features)
- [Contributing](#contributing)
- [License](#license)

---

## 🌟 Overview

Labour-Net is a Spring Boot-based web application designed to streamline the connection between skilled workers and employment agencies. The platform provides:

- **Workers**: Register, browse jobs, apply, and rate agencies
- **Agencies**: Post jobs, manage applications, view worker profiles
- **Admins**: Verify and approve users, monitor platform activity

---

## ✨ Features

### 👷 Worker Features
- ✅ Aadhaar-verified registration
- ✅ Webcam-based profile picture capture
- ✅ Job browsing and application
- ✅ Application tracking
- ✅ Agency rating and feedback system
- ✅ Secure password requirements

### 🏢 Agency Features
- ✅ License-verified registration
- ✅ Job posting and management
- ✅ Application review and approval
- ✅ Worker profile viewing
- ✅ Reply to applications
- ✅ Job status control (Open/Closed)

### 👨‍💼 Admin Features
- ✅ Worker and agency approval workflow
- ✅ Profile verification
- ✅ Platform monitoring
- ✅ User management
- ✅ Detailed profile views

### 🔐 Security Features
- ✅ BCrypt password encryption
- ✅ Session-based authentication
- ✅ Password validation (5+ chars, uppercase, lowercase, special char)
- ✅ Protected routes with interceptors
- ✅ Cache control to prevent back-button issues
- ✅ CSRF protection (configurable)

---

## 🛠️ Technologies Used

### Backend
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **Spring Security**
- **Hibernate**
- **MySQL 8.0**
- **BCrypt Password Encoder**

### Frontend
- **Thymeleaf** (Server-side rendering)
- **HTML5 / CSS3**
- **JavaScript** (Webcam API)
- **Bootstrap 5.3.3**
- **Google Fonts (Poppins)**

### Build Tools
- **Maven**
- **Lombok** (Code generation)

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/))
- **Git** ([Download](https://git-scm.com/downloads))

---

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/labour-net.git
cd labour-chain
```

### 2. Create MySQL Database

```sql
CREATE DATABASE miniproject;
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/miniproject
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**For better security**, use environment variables (see below).

### 4. Configure Email (Optional)

If you want to enable email features, update:

```properties
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

**Note:** For Gmail, you need to generate an [App Password](https://support.google.com/accounts/answer/185833).

### 5. Build the Project

```bash
mvn clean install
```

---

## ▶️ Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using JAR

```bash
mvn clean package
java -jar target/labour-chain-0.0.1-SNAPSHOT.jar
```

### Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

---

## 🔑 Test Credentials

The application comes pre-loaded with test data:

### Admin Login
- **URL**: `http://localhost:8080/admin/login`
- **Username**: `admin`
- **Password**: `admin123`

### Test Aadhaar Numbers (for Worker Registration)
- `123456789012` - John Worker
- `987654321098` - Jane Smith

### Test License Numbers (for Agency Registration)
- `LIC123456` - Agency Owner One
- `LIC789012` - Agency Owner Two

### Password Requirements
All passwords must have:
- Minimum 5 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 special character (@#$%^&+=!)

**Example valid password**: `Admin@123`

---

## 📁 Project Structure

```
labour-chain/
├── src/
│   ├── main/
│   │   ├── java/com/mini/labour_chain/
│   │   │   ├── config/              # Security & Configuration
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── model/               # JPA Entities
│   │   │   └── repository/          # Data Access Layer
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf HTML pages
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

---

## 🌐 API Endpoints

### Public Routes
- `GET /` - Home page
- `GET /workers` - Worker options (login/register)
- `GET /agencies` - Agency options (login/register)
- `GET /admin/login` - Admin login page
- `GET /jobs` - Browse all jobs

### Worker Routes (Authentication Required)
- `POST /workers/register` - Register new worker
- `POST /workers/login` - Worker login
- `GET /workers/dashboard` - Worker dashboard
- `GET /workers/logout` - Logout
- `GET /applications/worker/{id}` - View applications

### Agency Routes (Authentication Required)
- `POST /agencies/register` - Register new agency
- `POST /agencies/login` - Agency login
- `GET /agencies/dashboard` - Agency dashboard
- `GET /agencies/logout` - Logout
- `POST /jobs/post` - Post a new job
- `GET /jobs/edit/{id}` - Edit job
- `GET /jobs/delete/{id}` - Delete job
- `GET /jobs/toggle-status/{id}` - Toggle job status
- `POST /agencies/application/reply/{id}` - Reply to application
- `GET /agencies/application/approve/{id}` - Approve application
- `GET /agencies/application/reject/{id}` - Reject application

### Admin Routes (Authentication Required)
- `POST /admin/login` - Admin login
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/approve/worker/{id}` - Approve worker
- `GET /admin/approve/agency/{id}` - Approve agency
- `GET /admin/delete/worker/{id}` - Delete worker
- `GET /admin/delete/agency/{id}` - Delete agency
- `GET /admin/view/worker/{id}` - View worker profile
- `GET /admin/view/agency/{id}` - View agency profile

### Job Application Routes
- `GET /jobs/apply/{jobId}` - Application form
- `POST /jobs/apply/{jobId}` - Submit application
- `GET /applications/confirm/{applicationId}` - Confirm job
- `POST /applications/rate-agency/{applicationId}` - Rate agency

---

## 🔒 Security Features

### Password Security
- BCrypt hashing with salt
- Strong password validation
- Secure password storage

### Session Management
- HTTP-only session cookies
- 30-minute session timeout
- Cache control headers
- Session security interceptor

### Access Control
- Role-based route protection
- Session validation on protected routes
- Automatic redirect on unauthorized access

---

## 🌍 Environment Variables (Recommended for Production)

Instead of hardcoding sensitive data, use environment variables:

### Linux/Mac
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
```

### Windows (PowerShell)
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:MAIL_USERNAME="your_email@gmail.com"
$env:MAIL_PASSWORD="your_app_password"
```

Update `application.properties`:
```properties
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:admin}
spring.mail.username=${MAIL_USERNAME:yourgmail@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your_app_password}
```

---

## 🐛 Troubleshooting

### Database Connection Issues
```
Error: Communications link failure
```
**Solution**: Ensure MySQL is running and credentials are correct.

### Port Already in Use
```
Error: Port 8080 is already in use
```
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Webcam Not Working
**Solution**: Ensure HTTPS or localhost, and grant browser permissions.

---

## 📊 Database Schema

The application uses JPA to auto-generate tables. Key entities:

- **user** - Worker information
- **agency** - Agency information
- **admin** - Administrator accounts
- **job** - Job postings
- **job_application** - Job applications
- **aadhar_verification** - Aadhaar verification data
- **agency_verification** - License verification data

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

- **Your Name** - *Initial work*

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- Thymeleaf Documentation
- Stack Overflow Community
- Google Fonts (Poppins)

---

## 📞 Support

For support, email support@labournet.com or open an issue in the repository.

---

**Made with ❤️ using Spring Boot**
