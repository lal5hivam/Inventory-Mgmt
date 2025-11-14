# Environment Variables Quick Reference

## 🚀 Quick Setup Commands

### Generate JWT Secret (Required)
```bash
# Linux/Mac
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

---

## Backend Environment Variables

### Railway.app / Render.com / Heroku

Set these in your platform's environment variables dashboard:

```bash
# Required
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_URL=jdbc:mysql://your-db-host:3306/inventory_db?useSSL=true&requireSSL=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your_generated_jwt_secret
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=https://your-frontend-url.com
```

### Docker Run Command
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/inventory_db" \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  -e CORS_ALLOWED_ORIGINS=https://your-frontend.com \
  inventory-backend
```

### docker-compose.yml
```yaml
version: '3.8'
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:mysql://db:3306/inventory_db
      DB_USERNAME: root
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      CORS_ALLOWED_ORIGINS: ${FRONTEND_URL}
    depends_on:
      - db
  
  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: inventory_db
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## Frontend Environment Variables

### Vercel / Netlify

Create `.env.production`:

```bash
REACT_APP_API_BASE_URL=https://your-backend-url.com/api
REACT_APP_ENCRYPTION_KEY=your-unique-encryption-key
```

Or set in platform dashboard:
- Vercel: Settings → Environment Variables
- Netlify: Site settings → Environment variables

---

## Local Development

### Backend Local (.env or IDE)
```bash
SPRING_PROFILES_ACTIVE=dev
DB_PASSWORD=your_local_password
JWT_SECRET=dev-secret-key-change-in-production
```

### Frontend Local (.env.local)
```bash
REACT_APP_API_BASE_URL=http://localhost:5050/api
REACT_APP_ENCRYPTION_KEY=local-dev-key
```

---

## Platform-Specific Examples

### Railway CLI
```bash
railway variables set SPRING_PROFILES_ACTIVE=prod
railway variables set DB_URL="jdbc:mysql://..."
railway variables set JWT_SECRET="your-secret"
railway variables set CORS_ALLOWED_ORIGINS="https://your-frontend.com"
```

### Heroku CLI
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set DB_URL="jdbc:mysql://..."
heroku config:set JWT_SECRET="your-secret"
heroku config:set CORS_ALLOWED_ORIGINS="https://your-frontend.com"
```

### AWS Elastic Beanstalk
Create `.ebextensions/environment.config`:
```yaml
option_settings:
  - option_name: SPRING_PROFILES_ACTIVE
    value: prod
  - option_name: DB_URL
    value: jdbc:mysql://your-db:3306/inventory_db
  - option_name: JWT_SECRET
    value: your-secret
  - option_name: CORS_ALLOWED_ORIGINS
    value: https://your-frontend.com
```

### Kubernetes Secret
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  DB_PASSWORD: your_db_password
  JWT_SECRET: your_jwt_secret
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  SPRING_PROFILES_ACTIVE: "prod"
  DB_URL: "jdbc:mysql://mysql-service:3306/inventory_db"
  CORS_ALLOWED_ORIGINS: "https://your-frontend.com"
```

---

## Critical Security Notes

⚠️ **NEVER commit these values to Git:**
- DB_PASSWORD
- JWT_SECRET
- Any production credentials

✅ **Always use:**
- Environment variables in production
- Secrets management in your platform
- `.env.local` for local development (add to .gitignore)

✅ **Generate strong secrets:**
- JWT_SECRET: Minimum 32 random bytes (base64 encoded)
- DB_PASSWORD: Strong password with special characters
- Change all default values before deployment

---

## Quick Deployment Checklist

- [ ] Generated strong JWT secret
- [ ] Set all required environment variables
- [ ] Updated CORS_ALLOWED_ORIGINS to actual frontend URL
- [ ] Set SPRING_PROFILES_ACTIVE=prod
- [ ] Database credentials are secure
- [ ] Frontend has correct backend API URL
- [ ] Tested locally with production profile first
