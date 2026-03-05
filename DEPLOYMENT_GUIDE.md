# Production Deployment Guide

## Pre-Deployment Checklist

### 1. Environment Setup

```bash
# Generate strong JWT secret (minimum 256 bits)
openssl rand -base64 64

# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export DB_URL="jdbc:mysql://your-db-host:3306/resort_auth?useSSL=true&serverTimezone=UTC"
export DB_USERNAME="your_db_user"
export DB_PASSWORD="your_secure_password"
export JWT_SECRET="your-generated-secret-from-above"
export CORS_ALLOWED_ORIGINS="https://yourdomain.com,https://www.yourdomain.com"
```

### 2. Database Setup

```sql
-- Create production database
CREATE DATABASE resort_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create dedicated user (don't use root!)
CREATE USER 'auth_service'@'%' IDENTIFIED BY 'strong_password_here';
GRANT SELECT, INSERT, UPDATE, DELETE ON resort_auth.* TO 'auth_service'@'%';
FLUSH PRIVILEGES;

-- Enable SSL for database connections
-- Configure your MySQL server with SSL certificates
```

### 3. Security Configuration

```bash
# Update CORS origins in production
export CORS_ALLOWED_ORIGINS="https://yourdomain.com"

# Configure rate limiting (adjust based on your needs)
export RATE_LIMIT_CAPACITY=10
export LOCKOUT_MAX_ATTEMPTS=5
export LOCKOUT_DURATION_MINUTES=30
```

## Deployment Options

### Option 1: Docker Deployment (Recommended)

```bash
# 1. Build Docker image
docker build -t auth-service:1.0.0 .

# 2. Run with environment variables
docker run -d \
  --name auth-service \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:mysql://your-db:3306/resort_auth" \
  -e DB_USERNAME="auth_service" \
  -e DB_PASSWORD="your_password" \
  -e JWT_SECRET="your_jwt_secret" \
  -e CORS_ALLOWED_ORIGINS="https://yourdomain.com" \
  auth-service:1.0.0

# 3. Check logs
docker logs -f auth-service

# 4. Health check
curl http://localhost:8081/actuator/health
```

### Option 2: Docker Compose

```bash
# 1. Create production .env file
cp .env.example .env.prod
# Edit .env.prod with production values

# 2. Deploy
docker-compose --env-file .env.prod up -d

# 3. Monitor
docker-compose logs -f
```

### Option 3: Kubernetes Deployment

```yaml
# Create secrets
kubectl create secret generic auth-service-secrets \
  --from-literal=db-password='your_password' \
  --from-literal=jwt-secret='your_jwt_secret'

# Apply deployment
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

### Option 4: Traditional JAR Deployment

```bash
# 1. Build
mvn clean package -DskipTests

# 2. Run with production profile
java -jar \
  -Dspring.profiles.active=prod \
  -DDB_URL="jdbc:mysql://your-db:3306/resort_auth" \
  -DDB_USERNAME="auth_service" \
  -DDB_PASSWORD="your_password" \
  -DJWT_SECRET="your_jwt_secret" \
  target/authorization-service-0.0.1-SNAPSHOT.jar
```

## Post-Deployment

### 1. Verify Deployment

```bash
# Health check
curl https://yourdomain.com/actuator/health

# Expected response:
# {"status":"UP"}

# Test registration
curl -X POST https://yourdomain.com/api/auth/register/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123",
    "confirmPassword": "SecurePass123",
    "age": 25,
    "phoneNumber": "1234567890",
    "gender": "Male"
  }'
```

### 2. Configure Monitoring

```bash
# Prometheus metrics endpoint
curl https://yourdomain.com/actuator/prometheus

# Set up alerts for:
# - High error rates
# - Database connection failures
# - High response times
# - Rate limit violations
```

### 3. Set Up Log Aggregation

```bash
# Configure log shipping to:
# - ELK Stack (Elasticsearch, Logstash, Kibana)
# - Splunk
# - CloudWatch (AWS)
# - Stackdriver (GCP)

# Example: Filebeat configuration for ELK
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /var/log/auth-service/*.log
  json.keys_under_root: true
```

### 4. Database Backups

```bash
# Set up automated backups
# Example: Daily MySQL backup
0 2 * * * mysqldump -u backup_user -p resort_auth > /backups/resort_auth_$(date +\%Y\%m\%d).sql

# Test restore procedure regularly
mysql -u auth_service -p resort_auth < backup.sql
```

## SSL/TLS Configuration

### Using Nginx as Reverse Proxy

```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /etc/ssl/certs/yourdomain.crt;
    ssl_certificate_key /etc/ssl/private/yourdomain.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Scaling Considerations

### Horizontal Scaling

```bash
# Run multiple instances behind load balancer
docker run -d --name auth-service-1 -p 8081:8081 auth-service:1.0.0
docker run -d --name auth-service-2 -p 8082:8081 auth-service:1.0.0
docker run -d --name auth-service-3 -p 8083:8081 auth-service:1.0.0

# Configure load balancer (Nginx, HAProxy, AWS ALB)
```

### Database Connection Pool

```properties
# Adjust based on load
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
```

## Troubleshooting

### Common Issues

1. **Database Connection Failures**
```bash
# Check connectivity
telnet your-db-host 3306

# Verify credentials
mysql -h your-db-host -u auth_service -p

# Check logs
docker logs auth-service | grep -i "database"
```

2. **JWT Token Issues**
```bash
# Verify JWT_SECRET is set
echo $JWT_SECRET

# Check token generation in logs
docker logs auth-service | grep -i "jwt"
```

3. **Rate Limiting Too Aggressive**
```bash
# Adjust limits
export RATE_LIMIT_CAPACITY=20
export RATE_LIMIT_MINUTES=5

# Restart service
docker restart auth-service
```

## Rollback Procedure

```bash
# 1. Stop current version
docker stop auth-service

# 2. Start previous version
docker run -d --name auth-service auth-service:previous-version

# 3. Verify
curl http://localhost:8081/actuator/health

# 4. If database migration needed, restore backup
mysql -u auth_service -p resort_auth < backup_before_deployment.sql
```

## Security Hardening

1. **Enable Database SSL**
2. **Use secrets management** (AWS Secrets Manager, HashiCorp Vault)
3. **Enable audit logging**
4. **Set up WAF** (Web Application Firewall)
5. **Regular security updates**
6. **Penetration testing**
7. **OWASP dependency scanning**

## Maintenance

### Regular Tasks

- **Daily**: Monitor logs and metrics
- **Weekly**: Review security audit logs
- **Monthly**: Update dependencies, security patches
- **Quarterly**: Load testing, disaster recovery drills

## Support

For issues or questions:
- Check logs: `docker logs auth-service`
- Review metrics: `/actuator/metrics`
- Contact: [your-support-email]
