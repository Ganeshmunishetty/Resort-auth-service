#!/bin/bash

# Development Setup Script

echo "==================================="
echo "Authorization Service - Dev Setup"
echo "==================================="

# Check if .env exists
if [ ! -f .env ]; then
    echo "Creating .env file from template..."
    cp .env.example .env
    echo "✅ .env file created. Please edit it with your configuration."
else
    echo "✅ .env file already exists"
fi

# Generate JWT secret if needed
echo ""
echo "Generating JWT secret..."
JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
echo "Your JWT Secret (add to .env):"
echo "JWT_SECRET=$JWT_SECRET"

echo ""
echo "==================================="
echo "Next Steps:"
echo "==================================="
echo "1. Edit .env file with your database credentials"
echo "2. Start MySQL: docker run -d --name auth-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=resort_auth -p 3306:3306 mysql:8.0"
echo "3. Run application: ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev"
echo ""
echo "Or use Docker Compose:"
echo "docker-compose up -d"
