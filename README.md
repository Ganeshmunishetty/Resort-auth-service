# Authorization Service - Full Stack

A complete, production-ready authentication system with:
- **Backend**: Spring Boot REST API (in this folder)
- **Frontend**: Beautiful React UI (in ../auth-frontend folder)
- **Security**: JWT, account lockout, rate limiting, audit logging
- **Admin Panel**: User management dashboard

## 📁 Project Structure

The project is split into two separate folders:

```
auth/
├── authorization-service/  (THIS FOLDER - Backend)
│   ├── src/               # Spring Boot source code
│   ├── pom.xml           # Maven configuration
│   ├── start-all.bat     # Startup script for both
│   └── Documentation files
│
└── auth-frontend/         (SIBLING FOLDER - Frontend)
    ├── src/              # React source code
    ├── public/           # Static files
    └── package.json      # NPM dependencies
```

## Features

### Security
- ✅ JWT-based authentication with secure token generation
- ✅ Role-based access control (USER, OWNER, ADMIN)
- ✅ Rate limiting on authentication endpoints
- ✅ Account lockout after failed login attempts
- ✅ Secure password reset with token expiration
- ✅ Security audit logging
- ✅ Environment-based configuration (no hardcoded secrets)

### Production Features
- ✅ Profile-specific configurations (dev, staging, prod)
- ✅ Database connection pooling (HikariCP)
- ✅ Health checks and monitoring endpoints
- ✅ Docker containerization
- ✅ Comprehensive error handling
- ✅ Request correlation IDs
- ✅ Database indexes for performance

## 🚀 Quick Start - Full Stack

### Option 1: Use Startup Script (Windows)
```bash
# From this folder (authorization-service)
start-all.bat
```
This will start both backend and frontend automatically!

### Option 2: Manual Start

**Terminal 1 - Backend:**
```bash
# From this folder (authorization-service)
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
# From parent folder, then:
cd ../auth-frontend
npm install  # First time only
npm start
```

### Access Points
- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8081/api
- **API Documentation**: http://localhost:8081/swagger-ui.html

### Frontend Documentation
- Full guide: `FRONTEND_SETUP.md` (in this folder)
- Quick start: `../auth-frontend/QUICK_START.md`
- Complete system guide: `COMPLETE_SYSTEM_GUIDE.md`
- Docker (optional)

### Local Development

1. **Clone and setup environment**
```bash
cp .env.example .env
# Edit .env with your configuration
```

2. **Generate JWT Secret**
```bash
# Linux/Mac
openssl rand -base64 64

# Windows (PowerShell)
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

3. **Update .env file**
```env
JWT_SECRET=<your-generated-secret>
DB_PASSWORD=<your-mysql-password>
```

4. **Run MySQL**
```bash
docker run -d \
  --name auth-mysql \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=resort_auth \
  -p 3306:3306 \
  mysql:8.0
```

5. **Run Application**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker Deployment

```bash
# Build and run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f auth-service

# Stop services
docker-compose down
```

## API Endpoints

### Public Endpoints

#### Register User
```http
POST /api/auth/register/user
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "confirmPassword": "SecurePass123",
  "age": 25,
  "phoneNumber": "1234567890",
  "gender": "Male"
}
```

#### Login
```http
POST /api/auth/login/user
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

#### Forgot Password
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "john@example.com"
}
```

#### Reset Password
```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "reset-token-from-email",
  "newPassword": "NewSecurePass123",
  "confirmPassword": "NewSecurePass123"
}
```

### Protected Endpoints

```http
GET /api/auth/user/profile
Authorization: Bearer <jwt-token>
```

## Configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Active profile (dev/prod) | dev | No |
| `DB_URL` | Database connection URL | - | Yes |
| `DB_USERNAME` | Database username | - | Yes |
| `DB_PASSWORD` | Database password | - | Yes |
| `JWT_SECRET` | JWT signing secret (min 256 bits) | - | Yes |
| `JWT_EXPIRATION_MS` | JWT expiration time | 3600000 | No |
| `RATE_LIMIT_CAPACITY` | Max requests per window | 5 | No |
| `LOCKOUT_MAX_ATTEMPTS` | Failed attempts before lockout | 5 | No |
| `LOCKOUT_DURATION_MINUTES` | Lockout duration | 30 | No |

### Profiles

- **dev**: Development with verbose logging, relaxed security
- **prod**: Production with minimal logging, strict security

## Security Features

### Rate Limiting
- 5 requests per minute per IP on login/register endpoints
- Returns 429 Too Many Requests with retry-after header

### Account Lockout
- Account locked for 30 minutes after 5 failed login attempts
- Automatic unlock after duration expires
- Failed attempts reset on successful login

### Password Reset
- Token-based reset with 15-minute expiration
- Secure random token generation
- One-time use tokens

### Audit Logging
All security events are logged:
- Login success/failure
- Account lockouts
- Password resets
- Registration events
- Unauthorized access attempts

## Monitoring

### Health Check
```http
GET /actuator/health
```

### Metrics (Production)
```http
GET /actuator/metrics
GET /actuator/prometheus
```

## Database Schema

The application uses the following main table:

```sql
CREATE TABLE user_credentials (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL,
  failed_login_attempts INT DEFAULT 0,
  locked_until DATETIME,
  reset_token VARCHAR(255),
  reset_token_expiry DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME,
  INDEX idx_email (email),
  INDEX idx_username (username),
  INDEX idx_status (status)
);
```

## Production Deployment Checklist

- [ ] Set strong JWT_SECRET (minimum 256 bits)
- [ ] Use environment variables for all secrets
- [ ] Set SPRING_PROFILES_ACTIVE=prod
- [ ] Configure database with SSL
- [ ] Set up database backups
- [ ] Configure log aggregation (ELK, Splunk)
- [ ] Set up monitoring (Prometheus, Grafana)
- [ ] Configure CORS for your frontend domain
- [ ] Set up HTTPS/TLS
- [ ] Review and adjust rate limits
- [ ] Configure email service for password reset
- [ ] Set up CI/CD pipeline
- [ ] Perform security audit
- [ ] Load testing

## Next Steps

See [PRODUCTION_READINESS_PLAN.md](PRODUCTION_READINESS_PLAN.md) for:
- Testing strategy
- CI/CD setup
- Advanced monitoring
- Performance optimization
- Disaster recovery

## License

[Your License Here]


---

## 🎨 NEW: React Frontend UI

A beautiful, modern React frontend has been added in the `auth-frontend` folder!

### Frontend Features
- ✅ Material-UI design system
- ✅ Login page with role selection
- ✅ Registration page with validation
- ✅ Password reset flow
- ✅ User dashboard
- ✅ Admin dashboard with user management
- ✅ Responsive design for all devices
- ✅ Protected routes with authentication
- ✅ Role-based access control

### Quick Start - Full Stack

**Option 1: Use the startup script (Windows)**
```bash
start-all.bat
```

**Option 2: Manual start**

Terminal 1 - Backend:
```bash
./mvnw spring-boot:run
```

Terminal 2 - Frontend:
```bash
cd auth-frontend
npm install  # First time only
npm start
```

### Access Points
- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8081/api
- **API Documentation**: http://localhost:8081/swagger-ui.html

### Frontend Documentation
- Full guide: `FRONTEND_SETUP.md`
- Quick start: `auth-frontend/QUICK_START.md`
- Complete system guide: `COMPLETE_SYSTEM_GUIDE.md`

---
