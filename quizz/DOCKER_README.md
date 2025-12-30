# Docker Deployment Guide - Quiz Application

## 📋 Prerequisites

- Docker Desktop 20.10+ installed
- Docker Compose 2.0+ installed
- At least 2GB RAM available for containers

## 🚀 Quick Start

### 1. Build and Run with Docker Compose

```bash
# Navigate to project directory
cd quizz

# Build and start all services (App + PostgreSQL)
docker-compose up -d

# View logs
docker-compose logs -f quiz-app

# Check container status
docker-compose ps
```

### 2. Access the Application

- **API Base URL:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs
- **Health Check:** http://localhost:8080/actuator/health

### 3. Stop and Clean Up

```bash
# Stop containers
docker-compose down

# Stop and remove volumes (WARNING: This deletes all data)
docker-compose down -v

# Remove built images
docker-compose down --rmi all
```

## 🔧 Configuration

### Environment Variables

The application supports the following environment variables (configured in `docker-compose.yml`):

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://postgres:5432/quiz_db` | Database connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres123` | Database password |
| `JWT_SECRET` | `...` | JWT signing secret key |
| `JWT_EXPIRATION` | `86400000` | Access token expiration (24h) |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh token expiration (7 days) |

### Customize Configuration

Edit `docker-compose.yml` to change environment variables:

```yaml
environment:
  DB_PASSWORD: your_secure_password
  JWT_SECRET: your_secure_secret_key_here
```

## 🏗️ Build Details

### Multi-Stage Dockerfile

The Dockerfile uses a multi-stage build for optimal image size:

1. **Build Stage:** Uses `eclipse-temurin:21-jdk-alpine` to compile the application
2. **Runtime Stage:** Uses `eclipse-temurin:21-jre-alpine` (JRE only) for smaller final image

**Expected Image Size:** ~250-280 MB

### Verify Image Size

```bash
docker images quiz-app
```

## ✅ Health Checks

Both services include health checks to ensure proper startup order:

### PostgreSQL Health Check
- **Check Command:** `pg_isready -U postgres -d quiz_db`
- **Interval:** 10 seconds
- **Start Period:** 10 seconds

### Application Health Check
- **Check Command:** `wget http://localhost:8080/actuator/health`
- **Interval:** 30 seconds
- **Start Period:** 60 seconds (waits for DB to be ready)

### Check Health Status

```bash
# Check all services health
docker-compose ps

# Inspect specific service
docker inspect quiz-app --format='{{.State.Health.Status}}'
```

## 🐛 Troubleshooting

### Issue: Application fails to connect to database

**Solution:** Check if PostgreSQL is healthy:
```bash
docker-compose logs postgres
docker exec -it quiz-postgres pg_isready -U postgres
```

### Issue: Port already in use

**Solution:** Change ports in `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Change host port from 8080 to 8081
```

### Issue: Out of memory

**Solution:** Allocate more memory to Docker Desktop (Settings > Resources > Memory)

### Issue: Slow build time

**Solution:** The first build takes longer. Dependencies are cached after the first build.

## 🔐 Security Notes

1. **Change default passwords** in production:
   - Update `POSTGRES_PASSWORD` in docker-compose.yml
   - Update `JWT_SECRET` with a secure random key

2. **Non-root user:** The application runs as a non-root user (`spring`) inside the container for security

3. **Network isolation:** Services communicate through a dedicated Docker network (`quiz-network`)

## 📊 Monitoring

### View Application Logs

```bash
# Follow all logs
docker-compose logs -f

# Follow specific service
docker-compose logs -f quiz-app

# View last 100 lines
docker-compose logs --tail=100 quiz-app
```

### Access Container Shell

```bash
# Access application container
docker exec -it quiz-app sh

# Access PostgreSQL container
docker exec -it quiz-postgres psql -U postgres -d quiz_db
```

### Database Connection

```bash
# Connect to PostgreSQL from host
psql -h localhost -p 5432 -U postgres -d quiz_db

# Or using Docker
docker exec -it quiz-postgres psql -U postgres -d quiz_db
```

## 🎯 Testing the API

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "Admin123!",
    "fullName": "Admin User"
  }'
```

### 2. Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "Admin123!"
  }'
```

### 3. Test Protected Endpoint

```bash
curl -X GET http://localhost:8080/api/v1/quizzes \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🏆 Best Practices Implemented

✅ **Multi-stage build** - Reduced image size from ~600MB to ~270MB  
✅ **Health checks** - Ensures DB is ready before app starts  
✅ **Environment variables** - Secure configuration management  
✅ **Non-root user** - Enhanced security  
✅ **Volume persistence** - Data persists across container restarts  
✅ **Network isolation** - Services communicate securely  
✅ **.dockerignore** - Optimized build context  
✅ **Automatic restart** - Containers auto-restart on failure  

## 📦 Production Deployment

For production, consider:

1. Use Docker secrets for sensitive data
2. Set up reverse proxy (Nginx)
3. Enable HTTPS/TLS
4. Configure backup strategy for PostgreSQL volume
5. Use orchestration platform (Kubernetes, Docker Swarm)
6. Implement centralized logging (ELK stack)
7. Set up monitoring (Prometheus + Grafana)

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
