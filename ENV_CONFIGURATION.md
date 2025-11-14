# Environment Configuration Guide

This document explains how to properly configure environment variables for both the frontend and backend of the Inventory Management System.

## Frontend (React) Configuration

### Environment Files

The frontend uses React's built-in environment variable support. Variables must be prefixed with `REACT_APP_`.

#### Available Files:
- `.env.example` - Template file (commit to version control)
- `.env.local` - Local development (DO NOT commit)
- `.env.production` - Production build (DO NOT commit)

### Frontend Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `REACT_APP_API_BASE_URL` | Backend API URL | `http://localhost:5050/api` |
| `REACT_APP_ENCRYPTION_KEY` | Encryption key for localStorage | `phegon-dev-inventory` |

### Setup Instructions (Frontend)

1. **For Local Development:**
   ```bash
   # Copy the example file
   cp .env.example .env.local
   
   # Edit .env.local with your local values
   # Already configured with default values
   ```

2. **For Production:**
   ```bash
   # Edit .env.production with your production values
   REACT_APP_API_BASE_URL=https://your-production-api.com/api
   REACT_APP_ENCRYPTION_KEY=your-strong-production-key
   ```

3. **Run the application:**
   ```bash
   # Development
   npm start
   
   # Production build
   npm run build
   ```

---

## Backend (Spring Boot) Configuration

### Environment Files

The backend uses Spring profiles to manage different environments.

#### Available Files:
- `application.properties` - Main configuration (profile selector)
- `application-dev.properties` - Development environment (DO NOT commit)
- `application-prod.properties` - Production environment (DO NOT commit)

### Backend Environment Variables

#### Development (`application-dev.properties`):
- All secrets are in the file (for development only)
- Default database: `localhost:3306/inventory_db`
- Default credentials: `root` / `54335@Shivam`
- JWT Secret: `phegondev123456789phegondev123456789`

#### Production (`application-prod.properties`):
Uses environment variables for all secrets:

| Environment Variable | Description | Required |
|---------------------|-------------|----------|
| `SERVER_PORT` | Server port | No (default: 5050) |
| `DB_URL` | Database JDBC URL | Yes |
| `DB_USERNAME` | Database username | Yes |
| `DB_PASSWORD` | Database password | Yes |
| `JWT_SECRET` | JWT signing secret | Yes |
| `SPRING_PROFILES_ACTIVE` | Active profile | Yes (use 'prod') |

### Setup Instructions (Backend)

1. **For Local Development:**
   ```bash
   # Run with dev profile (default)
   mvn spring-boot:run
   
   # Or explicitly specify dev profile
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **For Production Deployment:**

   **Option A: Using Environment Variables**
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   export DB_URL=jdbc:mysql://prod-db-host:3306/inventory_db
   export DB_USERNAME=prod_user
   export DB_PASSWORD=strong_password_here
   export JWT_SECRET=your-strong-jwt-secret-key
   export SERVER_PORT=5050
   
   mvn spring-boot:run
   ```

   **Option B: Using Command Line Arguments**
   ```bash
   mvn spring-boot:run \
     -Dspring-boot.run.profiles=prod \
     -Dspring-boot.run.arguments="\
       --spring.datasource.url=jdbc:mysql://prod-db-host:3306/inventory_db \
       --spring.datasource.username=prod_user \
       --spring.datasource.password=strong_password \
       --secreteJwtString=your-jwt-secret"
   ```

   **Option C: Using External Configuration**
   Create a file outside the project:
   ```bash
   # /etc/inventory-app/application.properties
   spring.profiles.active=prod
   spring.datasource.url=jdbc:mysql://prod-db-host:3306/inventory_db
   spring.datasource.username=prod_user
   spring.datasource.password=strong_password
   secreteJwtString=your-jwt-secret
   ```
   
   Then run:
   ```bash
   java -jar target/InventoryMgtSystem.jar \
     --spring.config.location=/etc/inventory-app/application.properties
   ```

---

## Security Best Practices

### ✅ DO:
1. **Never commit sensitive data** to version control
2. **Use strong, unique secrets** for production
3. **Rotate secrets regularly** (JWT keys, encryption keys)
4. **Use environment variables** or external configuration in production
5. **Keep `.env.example`** updated as a template
6. **Generate strong JWT secrets** (at least 256 bits)
7. **Use different secrets** for dev and production
8. **Limit access** to production environment files

### ❌ DON'T:
1. **Don't commit** `.env.local`, `.env.production`, or `application-dev/prod.properties`
2. **Don't use development secrets** in production
3. **Don't share secrets** via email or chat
4. **Don't hardcode secrets** in source code
5. **Don't use weak or default passwords**

---

## Generating Secure Secrets

### For JWT Secret (Backend):
```bash
# Generate a random 256-bit key
openssl rand -base64 32
```

### For Encryption Key (Frontend):
```bash
# Generate a random key
openssl rand -hex 16
```

---

## Troubleshooting

### Frontend Issues:

**Problem:** Environment variables not loading
- **Solution:** Ensure variables start with `REACT_APP_`
- **Solution:** Restart the development server after changing `.env` files
- **Solution:** Check that `.env.local` exists

**Problem:** API connection fails
- **Solution:** Verify `REACT_APP_API_BASE_URL` matches your backend URL
- **Solution:** Check CORS configuration on backend

### Backend Issues:

**Problem:** Wrong profile is active
- **Solution:** Check `SPRING_PROFILES_ACTIVE` environment variable
- **Solution:** Verify `application.properties` default profile setting

**Problem:** Database connection fails
- **Solution:** Verify database credentials and URL
- **Solution:** Ensure MySQL is running
- **Solution:** Check network connectivity to database

**Problem:** JWT token errors
- **Solution:** Ensure `secreteJwtString` is properly set
- **Solution:** Use the same secret across all backend instances

---

## CI/CD Integration

### GitHub Actions Example:

```yaml
# .github/workflows/deploy.yml
env:
  REACT_APP_API_BASE_URL: ${{ secrets.API_BASE_URL }}
  REACT_APP_ENCRYPTION_KEY: ${{ secrets.ENCRYPTION_KEY }}
  DB_URL: ${{ secrets.DB_URL }}
  DB_USERNAME: ${{ secrets.DB_USERNAME }}
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
  JWT_SECRET: ${{ secrets.JWT_SECRET }}
```

Store secrets in GitHub repository settings under **Settings > Secrets and variables > Actions**.

---

## Quick Start Checklist

- [ ] Copy `.env.example` to `.env.local`
- [ ] Verify frontend environment variables in `.env.local`
- [ ] Verify backend credentials in `application-dev.properties`
- [ ] Ensure MySQL database is running
- [ ] Start backend: `cd backend && mvn spring-boot:run`
- [ ] Start frontend: `npm start`
- [ ] Test login functionality
- [ ] For production: Set up all production environment variables
- [ ] For production: Update `.env.production` with production API URL
- [ ] For production: Never commit sensitive environment files

---

## Support

If you encounter issues with environment configuration:
1. Check this documentation
2. Verify all environment variables are set correctly
3. Check application logs for detailed error messages
4. Ensure all services (database, backend, frontend) are running
