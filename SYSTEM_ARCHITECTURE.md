# System Architecture - Full Stack Authentication

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        USER BROWSER                         │
│                     http://localhost:3000                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/HTTPS
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    REACT FRONTEND                           │
│                   (auth-frontend/)                          │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   Login      │  │  Register    │  │   Forgot     │    │
│  │   Page       │  │   Page       │  │   Password   │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│                                                             │
│  ┌──────────────┐  ┌──────────────────────────────────┐  │
│  │   User       │  │   Admin Dashboard                │  │
│  │   Dashboard  │  │   - Pending Approvals            │  │
│  └──────────────┘  │   - All Users                    │  │
│                     │   - Approve/Reject/Unlock        │  │
│                     └──────────────────────────────────┘  │
│                                                             │
│  Components:                                                │
│  - Material-UI Design                                       │
│  - React Router (Navigation)                                │
│  - Axios (API Calls)                                        │
│  - Context API (State Management)                           │
│  - JWT Token Storage (localStorage)                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ REST API
                              │ JWT Bearer Token
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  SPRING BOOT BACKEND                        │
│                http://localhost:8081/api                    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              SECURITY FILTERS                       │  │
│  │  1. SecurityHeadersFilter (XSS, Clickjacking)      │  │
│  │  2. RateLimitFilter (5 req/min per IP)            │  │
│  │  3. JwtAuthenticationFilter (Token validation)     │  │
│  └─────────────────────────────────────────────────────┘  │
│                              │                              │
│                              ▼                              │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              REST CONTROLLERS                       │  │
│  │                                                     │  │
│  │  AuthController:                                    │  │
│  │  - POST /auth/register/{role}                      │  │
│  │  - POST /auth/login/{role}                         │  │
│  │  - POST /auth/forgot-password                      │  │
│  │  - POST /auth/reset-password                       │  │
│  │                                                     │  │
│  │  AdminController:                                   │  │
│  │  - GET  /admin/users/pending                       │  │
│  │  - GET  /admin/users                               │  │
│  │  - POST /admin/users/{id}/approve                  │  │
│  │  - POST /admin/users/{id}/reject                   │  │
│  │  - POST /admin/users/{id}/unlock                   │  │
│  └─────────────────────────────────────────────────────┘  │
│                              │                              │
│                              ▼                              │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              SERVICE LAYER                          │  │
│  │                                                     │  │
│  │  AuthService:                                       │  │
│  │  - User registration                                │  │
│  │  - User authentication                              │  │
│  │  - Password reset                                   │  │
│  │  - Account lockout logic                            │  │
│  │                                                     │  │
│  │  AdminService:                                      │  │
│  │  - User approval/rejection                          │  │
│  │  - Account management                               │  │
│  │  - Account unlock                                   │  │
│  │                                                     │  │
│  │  EmailService:                                      │  │
│  │  - Welcome emails                                   │  │
│  │  - Password reset emails                            │  │
│  │  - Approval notifications                           │  │
│  │                                                     │  │
│  │  JwtService:                                        │  │
│  │  - Token generation                                 │  │
│  │  - Token validation                                 │  │
│  │  - Token expiration                                 │  │
│  └─────────────────────────────────────────────────────┘  │
│                              │                              │
│                              ▼                              │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              REPOSITORY LAYER                       │  │
│  │  - UserCredentialRepository (JPA)                   │  │
│  │  - Custom queries                                   │  │
│  │  - Database operations                              │  │
│  └─────────────────────────────────────────────────────┘  │
│                              │                              │
│                              ▼                              │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              UTILITIES                              │  │
│  │  - JwtUtil (Token operations)                       │  │
│  │  - PasswordUtil (BCrypt)                            │  │
│  │  - SecurityAuditLogger                              │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ JDBC
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      MySQL DATABASE                         │
│                   jdbc:mysql://localhost:3306               │
│                                                             │
│  Database: resort_auth                                      │
│                                                             │
│  Table: user_credentials                                    │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ id (PK)                                             │  │
│  │ username (UNIQUE)                                   │  │
│  │ email (UNIQUE)                                      │  │
│  │ password (BCrypt)                                   │  │
│  │ role (USER/OWNER/ADMIN)                            │  │
│  │ status (PENDING/APPROVED/REJECTED)                 │  │
│  │ age                                                 │  │
│  │ gender                                              │  │
│  │ phone_number                                        │  │
│  │ failed_login_attempts                               │  │
│  │ locked_until                                        │  │
│  │ reset_token                                         │  │
│  │ reset_token_expiry                                  │  │
│  │ created_at                                          │  │
│  │ updated_at                                          │  │
│  │                                                     │  │
│  │ Indexes:                                            │  │
│  │ - idx_email                                         │  │
│  │ - idx_username                                      │  │
│  │ - idx_status                                        │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 🔄 Request Flow

### 1. User Registration Flow
```
User → Frontend (Register Page)
  ↓
  Fill form (username, email, password, etc.)
  ↓
  Click "Sign Up"
  ↓
Frontend → Backend (POST /api/auth/register/user)
  ↓
Backend → Validate input
  ↓
Backend → Check email/username uniqueness
  ↓
Backend → Hash password (BCrypt)
  ↓
Backend → Save to database
  ↓
Backend → Log registration (Audit)
  ↓
Backend → Send welcome email
  ↓
Backend → Return success response
  ↓
Frontend → Show success message
  ↓
Frontend → Redirect to login
```

### 2. User Login Flow
```
User → Frontend (Login Page)
  ↓
  Select role (User/Owner/Admin)
  ↓
  Enter email and password
  ↓
  Click "Sign In"
  ↓
Frontend → Backend (POST /api/auth/login/user)
  ↓
Backend → Rate limit check (5 req/min)
  ↓
Backend → Find user by email
  ↓
Backend → Check if account locked
  ↓
Backend → Verify password (BCrypt)
  ↓
Backend → Check role matches
  ↓
Backend → Check account approved
  ↓
Backend → Generate JWT token
  ↓
Backend → Reset failed attempts
  ↓
Backend → Log login success (Audit)
  ↓
Backend → Return token + user data
  ↓
Frontend → Store token in localStorage
  ↓
Frontend → Update auth context
  ↓
Frontend → Redirect to dashboard
```

### 3. Protected Route Access Flow
```
User → Frontend (Access /dashboard)
  ↓
Frontend → Check if authenticated
  ↓
  If NO → Redirect to /login
  ↓
  If YES → Continue
  ↓
Frontend → Make API call with JWT
  ↓
Backend → JWT Filter intercepts
  ↓
Backend → Validate JWT token
  ↓
Backend → Extract user info
  ↓
Backend → Check permissions
  ↓
Backend → Process request
  ↓
Backend → Return data
  ↓
Frontend → Display dashboard
```

### 4. Admin Approval Flow
```
Admin → Frontend (Admin Dashboard)
  ↓
  View pending users
  ↓
Frontend → Backend (GET /api/admin/users/pending)
  ↓
Backend → Check ADMIN role
  ↓
Backend → Query pending users
  ↓
Backend → Return user list
  ↓
Frontend → Display in table
  ↓
Admin → Click "Approve" button
  ↓
Frontend → Backend (POST /api/admin/users/{id}/approve)
  ↓
Backend → Check ADMIN role
  ↓
Backend → Update user status to APPROVED
  ↓
Backend → Send approval email
  ↓
Backend → Log action (Audit)
  ↓
Backend → Return success
  ↓
Frontend → Refresh user list
  ↓
Frontend → Show success message
```

## 🔐 Security Layers

### Layer 1: Frontend Security
- JWT token storage
- Protected routes
- Role-based UI rendering
- Input validation
- XSS prevention (React)

### Layer 2: Network Security
- HTTPS (production)
- CORS configuration
- Rate limiting headers
- Security headers

### Layer 3: Backend Security Filters
1. **SecurityHeadersFilter**
   - X-Frame-Options
   - X-Content-Type-Options
   - X-XSS-Protection
   - Strict-Transport-Security
   - Content-Security-Policy

2. **RateLimitFilter**
   - 5 requests per minute per IP
   - Token bucket algorithm
   - 429 Too Many Requests response

3. **JwtAuthenticationFilter**
   - Token validation
   - User authentication
   - Role extraction

### Layer 4: Application Security
- Password hashing (BCrypt)
- Account lockout (5 attempts)
- Token expiration (24 hours)
- Password reset tokens (15 min)
- Audit logging

### Layer 5: Database Security
- Unique constraints
- Indexes for performance
- Prepared statements (JPA)
- Connection pooling

## 📊 Data Flow

### Authentication Data
```
User Input → Frontend Validation → API Call → Backend Validation
  → Password Hashing → Database Storage → JWT Generation
  → Token Return → Frontend Storage → Authenticated State
```

### Authorization Data
```
API Request → JWT Token → Backend Validation → Role Extraction
  → Permission Check → Database Query → Data Return
  → Frontend Display
```

## 🔧 Technology Stack

### Frontend
- **React 18** - UI library
- **Material-UI** - Component library
- **React Router v6** - Routing
- **Axios** - HTTP client
- **Context API** - State management

### Backend
- **Spring Boot 3.3.5** - Framework
- **Spring Security** - Authentication/Authorization
- **JWT (jjwt)** - Token generation
- **Hibernate/JPA** - ORM
- **MySQL** - Database
- **Bucket4j** - Rate limiting
- **JavaMail** - Email service

### DevOps
- **Maven** - Build tool
- **Docker** - Containerization
- **Git** - Version control

## 📈 Scalability

### Horizontal Scaling
- Stateless backend (JWT)
- Load balancer ready
- Database connection pooling
- Caching can be added

### Vertical Scaling
- Optimized queries
- Database indexes
- Connection pooling
- Efficient algorithms

## 🔍 Monitoring

### Backend Monitoring
- Actuator health endpoints
- Security audit logs
- Application logs
- Database query logs

### Frontend Monitoring
- Browser console
- Network tab
- React DevTools
- Error boundaries (can be added)

## 🚀 Deployment Architecture

### Development
```
localhost:3000 (React Dev Server)
     ↓
localhost:8081 (Spring Boot)
     ↓
localhost:3306 (MySQL)
```

### Production Option 1 (Separate)
```
frontend.example.com (Netlify/Vercel)
     ↓
api.example.com (AWS/Heroku)
     ↓
RDS/Cloud SQL (MySQL)
```

### Production Option 2 (Integrated)
```
example.com (Spring Boot serves React build)
     ↓
RDS/Cloud SQL (MySQL)
```

## 📝 Summary

This architecture provides:
- ✅ Clear separation of concerns
- ✅ Scalable design
- ✅ Multiple security layers
- ✅ Easy to maintain
- ✅ Production-ready
- ✅ Well-documented
- ✅ Testable components

The system is designed to be secure, scalable, and maintainable for production use.
