# Deployment Checklist - Full Stack Authentication System

## ✅ Pre-Deployment Checklist

### Backend Verification
- [ ] All tests passing (20/20)
- [ ] Environment variables configured
- [ ] Database migrations ready
- [ ] JWT secret is strong and unique
- [ ] CORS configured for production domain
- [ ] Rate limiting configured
- [ ] Email service configured (SMTP)
- [ ] Logging configured
- [ ] Health check endpoint working
- [ ] API documentation accessible

### Frontend Verification
- [ ] Production build successful
- [ ] API URL points to production backend
- [ ] All pages load correctly
- [ ] Authentication flow works
- [ ] Protected routes work
- [ ] Admin features work
- [ ] Responsive design verified
- [ ] Browser compatibility tested
- [ ] No console errors
- [ ] Performance optimized

### Security Verification
- [ ] HTTPS enabled
- [ ] Security headers configured
- [ ] JWT tokens secure
- [ ] Passwords hashed (BCrypt)
- [ ] Account lockout working
- [ ] Rate limiting active
- [ ] CORS properly configured
- [ ] SQL injection protected (JPA)
- [ ] XSS protected (React + Headers)
- [ ] CSRF protected (JWT)

## 🚀 Deployment Steps

### Step 1: Prepare Backend

#### 1.1 Update Configuration
```properties
# application-prod.properties

# Server
server.port=8081

# Database (Production)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# CORS (Update with production domain)
cors.allowed.origins=https://yourdomain.com

# Email (Production SMTP)
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}
```

#### 1.2 Build Backend
```bash
./mvnw clean package -DskipTests
```

#### 1.3 Test JAR
```bash
java -jar target/authorization-service-0.0.1-SNAPSHOT.jar
```

### Step 2: Prepare Frontend

#### 2.1 Update API URL
```javascript
// src/services/api.js
const API_BASE_URL = 'https://api.yourdomain.com/api';
```

#### 2.2 Build Frontend
```bash
cd auth-frontend
npm run build
```

#### 2.3 Test Build
```bash
npm install -g serve
serve -s build -p 3000
```

### Step 3: Database Setup

#### 3.1 Create Production Database
```sql
CREATE DATABASE resort_auth_prod;
```

#### 3.2 Run Migrations
```bash
# Spring Boot will auto-create tables on first run
# Or use Flyway/Liquibase for controlled migrations
```

#### 3.3 Create Admin User
```sql
INSERT INTO user_credentials 
(username, email, password, role, status, age, gender, phone_number, failed_login_attempts, created_at, updated_at)
VALUES 
('admin', 'admin@yourdomain.com', '$2a$10$...', 'ADMIN', 'APPROVED', 30, 'Male', '1234567890', 0, NOW(), NOW());
```

### Step 4: Deploy Backend

#### Option A: AWS Elastic Beanstalk
```bash
# Install EB CLI
pip install awsebcli

# Initialize
eb init

# Create environment
eb create production

# Deploy
eb deploy
```

#### Option B: Heroku
```bash
# Login
heroku login

# Create app
heroku create your-app-name

# Add MySQL addon
heroku addons:create jawsdb:kitefin

# Deploy
git push heroku main
```

#### Option C: DigitalOcean
```bash
# Create droplet
# SSH into server
ssh root@your-server-ip

# Install Java
apt update
apt install openjdk-17-jdk

# Upload JAR
scp target/*.jar root@your-server-ip:/opt/app/

# Create systemd service
nano /etc/systemd/system/auth-service.service

# Start service
systemctl start auth-service
systemctl enable auth-service
```

### Step 5: Deploy Frontend

#### Option A: Netlify
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Login
netlify login

# Deploy
cd auth-frontend
netlify deploy --prod
```

#### Option B: Vercel
```bash
# Install Vercel CLI
npm install -g vercel

# Login
vercel login

# Deploy
cd auth-frontend
vercel --prod
```

#### Option C: Serve from Backend
```bash
# Copy build to Spring Boot static folder
cp -r auth-frontend/build/* src/main/resources/static/

# Rebuild backend
./mvnw clean package

# Deploy backend (includes frontend)
```

### Step 6: Configure Domain & SSL

#### 6.1 DNS Configuration
```
A Record: yourdomain.com → Backend IP
A Record: api.yourdomain.com → Backend IP
CNAME: www.yourdomain.com → yourdomain.com
```

#### 6.2 SSL Certificate
```bash
# Using Let's Encrypt
certbot --nginx -d yourdomain.com -d www.yourdomain.com
```

### Step 7: Environment Variables

#### Backend Environment Variables
```bash
export DB_URL="jdbc:mysql://production-db:3306/resort_auth_prod"
export DB_USERNAME="prod_user"
export DB_PASSWORD="strong_password_here"
export JWT_SECRET="very_long_random_secret_key_here"
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"
export SMTP_USERNAME="your-email@gmail.com"
export SMTP_PASSWORD="your-app-password"
```

#### Frontend Environment Variables
```bash
# .env.production
REACT_APP_API_URL=https://api.yourdomain.com/api
```

## 🔍 Post-Deployment Verification

### Backend Health Checks
```bash
# Health endpoint
curl https://api.yourdomain.com/actuator/health

# API documentation
curl https://api.yourdomain.com/swagger-ui.html

# Test registration
curl -X POST https://api.yourdomain.com/api/auth/register/user \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"Test123","confirmPassword":"Test123","age":25,"gender":"Male","phoneNumber":"1234567890"}'
```

### Frontend Checks
- [ ] Homepage loads
- [ ] Login page works
- [ ] Registration works
- [ ] Dashboard accessible
- [ ] Admin panel works
- [ ] All images load
- [ ] No 404 errors
- [ ] Mobile responsive

### Security Checks
```bash
# Check SSL
curl -I https://yourdomain.com

# Check security headers
curl -I https://api.yourdomain.com/api/auth/login/user

# Check CORS
curl -H "Origin: https://yourdomain.com" \
  -H "Access-Control-Request-Method: POST" \
  -X OPTIONS https://api.yourdomain.com/api/auth/login/user
```

## 📊 Monitoring Setup

### Backend Monitoring
```bash
# Application logs
tail -f /var/log/auth-service/application.log

# Security audit logs
tail -f /var/log/auth-service/security-audit.log

# System resources
htop
```

### Frontend Monitoring
- Set up Google Analytics
- Set up error tracking (Sentry)
- Monitor Core Web Vitals
- Set up uptime monitoring

### Database Monitoring
- Monitor connection pool
- Monitor query performance
- Set up automated backups
- Monitor disk space

## 🔄 Rollback Plan

### If Deployment Fails

#### Backend Rollback
```bash
# Revert to previous version
eb deploy --version previous-version

# Or restore from backup
systemctl stop auth-service
cp /backup/app.jar /opt/app/app.jar
systemctl start auth-service
```

#### Frontend Rollback
```bash
# Netlify
netlify rollback

# Vercel
vercel rollback

# Manual
git revert HEAD
npm run build
netlify deploy --prod
```

#### Database Rollback
```bash
# Restore from backup
mysql -u root -p resort_auth_prod < backup.sql
```

## 📝 Post-Deployment Tasks

### Immediate (Day 1)
- [ ] Monitor error logs
- [ ] Check user registrations
- [ ] Verify email delivery
- [ ] Test all critical flows
- [ ] Monitor server resources

### Short Term (Week 1)
- [ ] Review security logs
- [ ] Check performance metrics
- [ ] Gather user feedback
- [ ] Fix any issues
- [ ] Optimize as needed

### Long Term (Month 1)
- [ ] Review analytics
- [ ] Plan feature updates
- [ ] Security audit
- [ ] Performance optimization
- [ ] Documentation updates

## 🚨 Emergency Contacts

```
Backend Issues: backend-team@company.com
Frontend Issues: frontend-team@company.com
Database Issues: dba@company.com
Security Issues: security@company.com
```

## 📞 Support Resources

- Documentation: https://docs.yourdomain.com
- Status Page: https://status.yourdomain.com
- Support Email: support@yourdomain.com
- Emergency Hotline: +1-XXX-XXX-XXXX

## ✅ Final Checklist

Before going live:
- [ ] All tests passing
- [ ] Security audit completed
- [ ] Performance tested
- [ ] Backup strategy in place
- [ ] Monitoring configured
- [ ] Documentation updated
- [ ] Team trained
- [ ] Rollback plan ready
- [ ] Support team ready
- [ ] Announcement prepared

## 🎉 Go Live!

Once all checks pass:
1. Switch DNS to production
2. Monitor closely for 24 hours
3. Announce to users
4. Celebrate! 🎊

---

**Remember**: Always test in staging before production!
