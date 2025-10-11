# 🚀 Labour-Net Deployment Guide

This guide covers multiple deployment options for the Labour-Net application.

---

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Local Deployment](#local-deployment)
- [Docker Deployment](#docker-deployment)
- [Cloud Deployment](#cloud-deployment)
- [Production Checklist](#production-checklist)
- [Monitoring & Maintenance](#monitoring--maintenance)

---

## 🔧 Prerequisites

### All Deployments
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### Cloud Deployments
- Cloud provider account (AWS/Azure/GCP/Heroku)
- Domain name (optional)
- SSL certificate (recommended)

---

## 💻 Local Deployment

### Step 1: Clone and Build

```bash
git clone https://github.com/yourusername/labour-net.git
cd labour-chain
mvn clean package
```

### Step 2: Configure Database

```sql
CREATE DATABASE miniproject;
CREATE USER 'labournet'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON miniproject.* TO 'labournet'@'localhost';
FLUSH PRIVILEGES;
```

### Step 3: Set Environment Variables

**Linux/Mac:**
```bash
export DB_USERNAME=labournet
export DB_PASSWORD=secure_password
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
```

**Windows (PowerShell):**
```powershell
$env:DB_USERNAME="labournet"
$env:DB_PASSWORD="secure_password"
$env:MAIL_USERNAME="your_email@gmail.com"
$env:MAIL_PASSWORD="your_app_password"
```

### Step 4: Run Application

```bash
java -jar target/labour-chain-0.0.1-SNAPSHOT.jar
```

Application will be available at: `http://localhost:8080`

---

## 🐳 Docker Deployment

### Option 1: Docker Compose (Recommended)

**1. Create `docker-compose.yml`:**

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: labour-net-db
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: miniproject
      MYSQL_USER: labournet
      MYSQL_PASSWORD: labournet123
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - labour-net-network

  app:
    build: .
    container_name: labour-net-app
    environment:
      DB_URL: jdbc:mysql://mysql:3306/miniproject
      DB_USERNAME: labournet
      DB_PASSWORD: labournet123
      MAIL_USERNAME: ${MAIL_USERNAME}
      MAIL_PASSWORD: ${MAIL_PASSWORD}
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - labour-net-network

volumes:
  mysql-data:

networks:
  labour-net-network:
    driver: bridge
```

**2. Create `Dockerfile`:**

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/labour-chain-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**3. Create `.env` file:**

```env
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

**4. Deploy:**

```bash
docker-compose up -d
```

**5. View Logs:**

```bash
docker-compose logs -f app
```

**6. Stop:**

```bash
docker-compose down
```

### Option 2: Individual Docker Containers

**1. Build Image:**

```bash
docker build -t labour-net:latest .
```

**2. Run MySQL:**

```bash
docker run -d \
  --name labour-net-db \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=miniproject \
  -e MYSQL_USER=labournet \
  -e MYSQL_PASSWORD=labournet123 \
  -p 3306:3306 \
  mysql:8.0
```

**3. Run Application:**

```bash
docker run -d \
  --name labour-net-app \
  --link labour-net-db:mysql \
  -e DB_URL=jdbc:mysql://mysql:3306/miniproject \
  -e DB_USERNAME=labournet \
  -e DB_PASSWORD=labournet123 \
  -p 8080:8080 \
  labour-net:latest
```

---

## ☁️ Cloud Deployment

### AWS Elastic Beanstalk

**1. Install AWS CLI and EB CLI:**

```bash
pip install awscli awsebcli
aws configure
```

**2. Initialize Elastic Beanstalk:**

```bash
eb init -p java-17 labour-net
```

**3. Create Environment:**

```bash
eb create labour-net-env
```

**4. Set Environment Variables:**

```bash
eb setenv DB_URL=jdbc:mysql://your-rds-endpoint:3306/miniproject \
  DB_USERNAME=admin \
  DB_PASSWORD=yourpassword \
  MAIL_USERNAME=your_email@gmail.com \
  MAIL_PASSWORD=your_app_password
```

**5. Deploy:**

```bash
mvn clean package
eb deploy
```

**6. Open Application:**

```bash
eb open
```

### Heroku

**1. Install Heroku CLI:**

```bash
# Mac
brew install heroku/brew/heroku

# Windows
# Download from https://devcenter.heroku.com/articles/heroku-cli
```

**2. Login and Create App:**

```bash
heroku login
heroku create labour-net-app
```

**3. Add MySQL Database:**

```bash
heroku addons:create cleardb:ignite
```

**4. Get Database URL:**

```bash
heroku config:get CLEARDB_DATABASE_URL
```

**5. Set Environment Variables:**

```bash
heroku config:set DB_URL=jdbc:mysql://your-cleardb-url
heroku config:set DB_USERNAME=your_username
heroku config:set DB_PASSWORD=your_password
heroku config:set MAIL_USERNAME=your_email@gmail.com
heroku config:set MAIL_PASSWORD=your_app_password
```

**6. Deploy:**

```bash
git push heroku main
```

**7. Open Application:**

```bash
heroku open
```

### AWS EC2 (Manual)

**1. Launch EC2 Instance:**
- AMI: Amazon Linux 2 or Ubuntu 20.04
- Instance Type: t2.medium or higher
- Security Group: Allow ports 22, 80, 443, 8080

**2. Connect to Instance:**

```bash
ssh -i your-key.pem ec2-user@your-instance-ip
```

**3. Install Java and MySQL:**

```bash
# Amazon Linux
sudo yum update -y
sudo yum install java-17-amazon-corretto -y
sudo yum install mysql -y

# Ubuntu
sudo apt update
sudo apt install openjdk-17-jdk -y
sudo apt install mysql-server -y
```

**4. Transfer Application:**

```bash
scp -i your-key.pem target/labour-chain-0.0.1-SNAPSHOT.jar ec2-user@your-instance-ip:~/
```

**5. Run Application:**

```bash
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
nohup java -jar labour-chain-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

**6. Setup as System Service:**

Create `/etc/systemd/system/labour-net.service`:

```ini
[Unit]
Description=Labour Net Application
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar /home/ec2-user/labour-chain-0.0.1-SNAPSHOT.jar
Restart=on-failure
Environment="DB_USERNAME=root"
Environment="DB_PASSWORD=yourpassword"
Environment="MAIL_USERNAME=your_email@gmail.com"
Environment="MAIL_PASSWORD=your_app_password"

[Install]
WantedBy=multi-user.target
```

**Enable and Start:**

```bash
sudo systemctl daemon-reload
sudo systemctl enable labour-net
sudo systemctl start labour-net
sudo systemctl status labour-net
```

---

## ✅ Production Checklist

### Security

- [ ] Change default admin password
- [ ] Use strong database passwords
- [ ] Enable HTTPS/SSL
- [ ] Set `server.servlet.session.cookie.secure=true`
- [ ] Configure CSRF protection if needed
- [ ] Use environment variables for all secrets
- [ ] Restrict database access to application server only
- [ ] Enable firewall rules
- [ ] Regular security updates

### Database

- [ ] Setup database backups
- [ ] Configure connection pooling
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` (not update)
- [ ] Enable database monitoring
- [ ] Optimize indexes
- [ ] Setup read replicas (if needed)

### Application

- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure logging level to INFO or WARN
- [ ] Setup centralized logging (ELK stack, CloudWatch)
- [ ] Configure health checks
- [ ] Setup monitoring (Prometheus, New Relic)
- [ ] Enable graceful shutdown
- [ ] Configure thread pool sizes
- [ ] Setup load balancer (if multiple instances)

### Email

- [ ] Verify email credentials
- [ ] Setup email templates
- [ ] Configure rate limiting
- [ ] Test email delivery

### Performance

- [ ] Enable caching (Redis)
- [ ] Configure CDN for static assets
- [ ] Enable Gzip compression
- [ ] Optimize database queries
- [ ] Setup connection pooling
- [ ] Configure JVM parameters

### Monitoring

- [ ] Setup application monitoring
- [ ] Configure alerting
- [ ] Enable error tracking (Sentry, Rollbar)
- [ ] Setup uptime monitoring
- [ ] Configure log aggregation

---

## 📊 Monitoring & Maintenance

### Application Health Check

Spring Boot Actuator endpoints:

```properties
# Add to application.properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

Access: `http://your-domain:8080/actuator/health`

### Log Management

**View Logs:**

```bash
# Docker
docker logs -f labour-net-app

# SystemD
journalctl -u labour-net -f

# File
tail -f /var/log/labour-net/application.log
```

### Database Backup

**Automated Backup Script:**

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/mysql"
mkdir -p $BACKUP_DIR

mysqldump -u labournet -p'password' miniproject > $BACKUP_DIR/backup_$DATE.sql
gzip $BACKUP_DIR/backup_$DATE.sql

# Keep only last 7 days
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
```

**Add to Cron:**

```bash
crontab -e
# Add: 0 2 * * * /path/to/backup-script.sh
```

### Performance Tuning

**JVM Options:**

```bash
java -Xms512m -Xmx2048m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -jar labour-chain-0.0.1-SNAPSHOT.jar
```

### SSL/HTTPS Configuration

**Using Nginx as Reverse Proxy:**

```nginx
server {
    listen 80;
    server_name labournet.com www.labournet.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name labournet.com www.labournet.com;

    ssl_certificate /etc/ssl/certs/labournet.crt;
    ssl_certificate_key /etc/ssl/private/labournet.key;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 🆘 Troubleshooting

### Application Won't Start

```bash
# Check if port is in use
netstat -tuln | grep 8080

# Kill process using port
kill -9 $(lsof -t -i:8080)

# Check logs
tail -f application.log
```

### Database Connection Failed

```bash
# Test MySQL connection
mysql -h localhost -u labournet -p

# Check MySQL status
systemctl status mysql

# Verify credentials in application.properties
```

### Out of Memory Error

```bash
# Increase heap size
java -Xmx2048m -jar labour-chain-0.0.1-SNAPSHOT.jar

# Monitor memory
jmap -heap <pid>
```

---

## 📞 Support

For deployment issues:
- Check logs first
- Review configuration
- Test database connectivity
- Verify environment variables

For help: Open an issue on GitHub

---

**Happy Deploying! 🚀**
