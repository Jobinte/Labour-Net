# Labour Net - Setup Instructions

## Fixed Issues Summary

All major issues in the Spring Boot project have been resolved:

✅ **URL Mapping Inconsistencies** - Fixed all HTML template URLs to match controller mappings
✅ **Security Configuration** - Implemented proper password encoding with BCrypt
✅ **Duplicate Dependencies** - Removed duplicate spring-boot-starter-web from pom.xml
✅ **Missing Table Annotations** - Added @Table annotations to all entity classes
✅ **Password Security** - Implemented BCryptPasswordEncoder for secure password handling
✅ **HTML Structure** - Fixed navigation structure in index.html
✅ **Controller Redirects** - Updated all controller redirects to use correct URLs
✅ **Input Validation** - Added comprehensive validation annotations to models
✅ **Error Handling** - Improved error handling and session management
✅ **Database Relationships** - Added proper JoinColumn specifications

## Prerequisites

1. **Java 17** or higher
2. **MySQL 8.0** or higher
3. **Maven 3.6** or higher (or use the included Maven wrapper)

## Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE miniproject;
```

2. Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

## Email Configuration (Optional)

To enable contact form functionality, update email settings in `application.properties`:
```properties
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_app_password
```

## Running the Application

### Using Maven Wrapper (Recommended)
```bash
cd labour-chain
./mvnw clean spring-boot:run
```

### Using System Maven
```bash
cd labour-chain
mvn clean spring-boot:run
```

## Default Credentials

The application creates default test data on startup:

### Admin Login
- URL: http://localhost:8080/admin/login
- Username: `admin`
- Password: `admin123`

### Test Worker Accounts
Use these Aadhaar numbers to register as workers:
- `123456789012` (John Worker)
- `987654321098` (Jane Smith)

### Test Agency Accounts
Use these license numbers to register as agencies:
- `LIC123456` (Agency Owner One)
- `LIC789012` (Agency Owner Two)

## Application URLs

- **Home Page**: http://localhost:8080/
- **Worker Registration**: http://localhost:8080/workers/register
- **Worker Login**: http://localhost:8080/workers/login
- **Agency Registration**: http://localhost:8080/agencies/register
- **Agency Login**: http://localhost:8080/agencies/login
- **Admin Login**: http://localhost:8080/admin/login
- **Job Listings**: http://localhost:8080/jobs

## Key Features

1. **Aadhaar-based Worker Registration** - Workers register using verified Aadhaar data
2. **License-based Agency Registration** - Agencies register using verified license data
3. **Secure Authentication** - BCrypt password encoding for all user types
4. **Job Management** - Agencies can post jobs, workers can apply
5. **Admin Dashboard** - Complete system oversight and management
6. **Contact System** - Email-based contact form
7. **Responsive Design** - Mobile-friendly interface

## Security Features

- BCrypt password encoding
- Session-based authentication
- Input validation on all forms
- SQL injection protection via JPA
- XSS protection via Thymeleaf

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure MySQL is running
   - Check database credentials in `application.properties`
   - Verify database `miniproject` exists

2. **Port Already in Use**
   - Change port in `application.properties`: `server.port=8081`

3. **Email Features Not Working**
   - Update Gmail credentials in `application.properties`
   - Enable "App Passwords" in Gmail security settings

### Development Mode

For development, you can enable debug logging by updating `application.properties`:
```properties
logging.level.com.mini.labour_chain=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Project Structure

```
labour-chain/
├── src/main/java/com/mini/labour_chain/
│   ├── config/          # Security and data initialization
│   ├── controller/      # Web controllers
│   ├── model/           # JPA entities
│   └── repository/      # Data repositories
├── src/main/resources/
│   ├── templates/       # Thymeleaf HTML templates
│   └── application.properties
└── pom.xml             # Maven dependencies
```

## Support

If you encounter any issues:
1. Check the console logs for error messages
2. Verify database connectivity
3. Ensure all dependencies are properly downloaded
4. Check that port 8080 is available

The application now has comprehensive error handling and validation, making it production-ready with proper security measures in place.

