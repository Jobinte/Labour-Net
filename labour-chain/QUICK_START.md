# ⚡ Quick Start Guide - Labour-Net

Get up and running with Labour-Net in 5 minutes!

---

## 🎯 Prerequisites Checklist

Before you start, ensure you have:

- [ ] **Java 17+** installed ([Download](https://www.oracle.com/java/technologies/downloads/))
- [ ] **Maven 3.6+** installed ([Download](https://maven.apache.org/download.cgi))
- [ ] **MySQL 8.0+** installed and running ([Download](https://dev.mysql.com/downloads/))
- [ ] **Git** installed ([Download](https://git-scm.com/downloads))

**Verify installations:**

```bash
java -version      # Should show Java 17+
mvn -version       # Should show Maven 3.6+
mysql --version    # Should show MySQL 8.0+
```

---

## 🚀 Installation (5 Steps)

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/labour-net.git
cd labour-chain
```

### Step 2: Create Database

**Option A: Command Line**
```bash
mysql -u root -p
```

Then run:
```sql
CREATE DATABASE miniproject;
EXIT;
```

**Option B: MySQL Workbench**
- Open MySQL Workbench
- Create new schema named `miniproject`

### Step 3: Configure Application

Copy the example configuration:

```bash
cp application.properties.example src/main/resources/application.properties
```

Edit `src/main/resources/application.properties`:

```properties
# Update these values
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**Note:** Email configuration is optional for basic functionality.

### Step 4: Build Project

```bash
mvn clean install
```

**Expected output:** `BUILD SUCCESS`

### Step 5: Run Application

```bash
mvn spring-boot:run
```

**Wait for:** `Started LabourChainApplication in X seconds`

---

## 🎉 Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

You should see the Labour-Net homepage!

---

## 🔑 Test Login Credentials

### Admin Access

**URL:** `http://localhost:8080/admin/login`

```
Username: admin
Password: admin123
```

**Admin can:**
- Approve/reject workers and agencies
- View all user profiles
- Delete accounts
- Monitor platform activity

### Register as Worker

**URL:** `http://localhost:8080/workers/register`

**Use these test Aadhaar numbers:**
- `123456789012` - John Worker
- `987654321098` - Jane Smith

**Password requirements:**
- Minimum 5 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 special character (@#$%^&+=!)

**Example password:** `Worker@123`

**Steps:**
1. Enter username
2. Create password (follow requirements)
3. Enter Aadhaar number
4. Select role (e.g., Plumber, Electrician)
5. Enter emergency contact (10 digits)
6. Capture profile picture via webcam
7. Click Register
8. Wait for admin approval

### Register as Agency

**URL:** `http://localhost:8080/agencies/register`

**Use these test license numbers:**
- `LIC123456` - Agency Owner One
- `LIC789012` - Agency Owner Two

**Password requirements:** Same as worker

**Example password:** `Agency@123`

**Steps:**
1. Enter username
2. Create password
3. Enter license number
4. Enter agency name
5. Write bio (optional)
6. Click Register
7. Wait for admin approval

---

## 📋 Complete Workflow Test

### 1. Approve Users (Admin)

1. Login as admin
2. Go to Admin Dashboard
3. Click "Approve" for the worker you registered
4. Click "Approve" for the agency you registered

### 2. Post a Job (Agency)

1. Logout from admin
2. Login as the agency
3. Go to Agency Dashboard
4. Click "Post New Job"
5. Fill in job details:
   - Title: "Need Plumber"
   - Description: "Fix bathroom pipes"
   - Location: "Mumbai"
   - Salary: 5000
6. Click "Post Job"

### 3. Apply for Job (Worker)

1. Logout from agency
2. Login as the worker
3. Go to "Browse Jobs"
4. Find the posted job
5. Click "Apply"
6. Write a cover letter
7. Click "Submit Application"

### 4. Manage Application (Agency)

1. Logout from worker
2. Login as agency
3. Go to Agency Dashboard
4. View applications
5. Click "Approve" for the application
6. Optionally, send a reply

### 5. Rate Agency (Worker)

1. Logout from agency
2. Login as worker
3. Go to "My Applications"
4. Click "Confirm Job"
5. Rate the agency (1-5 stars)
6. Write feedback
7. Submit rating

**Congratulations! You've completed the full workflow! 🎉**

---

## 🛠️ Troubleshooting

### Application Won't Start

**Error:** `Port 8080 is already in use`

**Solution 1:** Kill process using the port (Windows PowerShell)
```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
```

**Solution 2:** Change port in `application.properties`
```properties
server.port=8081
```

### Database Connection Failed

**Error:** `Communications link failure`

**Solutions:**
1. Ensure MySQL is running:
   ```bash
   # Windows
   net start MySQL80
   
   # Linux
   sudo systemctl start mysql
   ```

2. Verify database exists:
   ```bash
   mysql -u root -p -e "SHOW DATABASES;"
   ```

3. Check credentials in `application.properties`

### Can't Capture Profile Picture

**Error:** Webcam not accessible

**Solutions:**
1. Grant browser permission to access camera
2. Ensure you're on `localhost` (HTTPS not required)
3. Check if another application is using the camera
4. Try a different browser

### Admin Password Not Working

**Solution:** The default password is `admin123`. If changed, check `DataInitializer.java`.

### Maven Build Failed

**Solution:** Clean and retry:
```bash
mvn clean
mvn install -U
```

### Email Not Working

**Note:** Email features are optional. The app works without email configuration.

**To enable:**
1. Use Gmail with 2FA
2. Generate App Password: [instructions](https://support.google.com/accounts/answer/185833)
3. Update `application.properties`:
   ```properties
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password
   ```

---

## 📚 Next Steps

Once you're running:

1. **Read the full README:** `README.md`
2. **Explore the codebase:** Check controllers, models, and templates
3. **Customize:** Modify colors, text, or features
4. **Deploy:** See `DEPLOYMENT.md` for deployment options
5. **Contribute:** Check `CONTRIBUTING.md` to contribute

---

## 🆘 Still Need Help?

- **Check logs:** Look at console output for errors
- **Read documentation:** See `README.md` for detailed info
- **Open an issue:** Create a GitHub issue with:
  - Error message
  - Steps to reproduce
  - Your environment (OS, Java version, etc.)

---

## 🎓 Learn More

### Technologies Used
- **Spring Boot:** Java framework for web applications
- **Thymeleaf:** Template engine for server-side rendering
- **MySQL:** Relational database
- **Spring Security:** Authentication and authorization
- **Bootstrap:** CSS framework

### Project Structure
```
labour-chain/
├── src/main/java/          # Java source files
│   └── controller/         # Request handlers
│   └── model/              # Data entities
│   └── repository/         # Database access
│   └── config/             # Configuration
├── src/main/resources/
│   └── templates/          # HTML pages
│   └── application.properties
├── pom.xml                 # Maven dependencies
└── README.md               # Full documentation
```

---

## ✅ Quick Reference

### URLs
- **Home:** http://localhost:8080
- **Workers:** http://localhost:8080/workers
- **Agencies:** http://localhost:8080/agencies
- **Admin:** http://localhost:8080/admin/login
- **Jobs:** http://localhost:8080/jobs

### Commands
```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Package as JAR
mvn clean package

# Run JAR
java -jar target/labour-chain-0.0.1-SNAPSHOT.jar
```

### Default Credentials
- **Admin:** username=`admin`, password=`admin123`
- **Test Aadhaar:** `123456789012`, `987654321098`
- **Test License:** `LIC123456`, `LIC789012`

---

**You're all set! Start building something amazing! 🚀**
