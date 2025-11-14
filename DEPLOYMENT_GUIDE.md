# 🚀 DEPLOYMENT GUIDE - Inventory Management System

## 📋 Table of Contents
1. [Pre-Deployment Checklist](#pre-deployment-checklist)
2. [Environment Variables Setup](#environment-variables-setup)
3. [Backend Deployment](#backend-deployment)
4. [Frontend Deployment](#frontend-deployment)
5. [Database Setup](#database-setup)
6. [Post-Deployment Verification](#post-deployment-verification)

---

## ✅ Pre-Deployment Checklist

### Before You Deploy:

- [ ] **Generate Strong JWT Secret**
  ```bash
  # On Linux/Mac
  openssl rand -base64 32
  
  # On Windows (PowerShell)
  [Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
  ```

- [ ] **Set Up Production Database**
  - Create MySQL database
  - Note down: host, port, database name, username, password
  - Ensure SSL/TLS is enabled

- [ ] **Prepare Frontend URL**
  - Know your frontend production URL (e.g., https://yourapp.vercel.app)

- [ ] **Choose Deployment Platforms**
  - Backend: Railway, Render, AWS, Heroku, etc.
  - Frontend: Vercel, Netlify, AWS Amplify, etc.

---

## 🔐 Environment Variables Setup

### Backend Environment Variables (Required)

Create these environment variables in your backend deployment platform:

```bash
# Profile
SPRING_PROFILES_ACTIVE=prod

# Server
SERVER_PORT=8080

# Database
DB_URL=jdbc:mysql://your-db-host:3306/inventory_db?useSSL=true&requireSSL=true
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

# JWT (CRITICAL - Use the secret you generated)
JWT_SECRET=your_generated_jwt_secret_here
JWT_EXPIRATION_MS=86400000

# CORS (CRITICAL - Your frontend URL)
CORS_ALLOWED_ORIGINS=https://your-frontend-url.com

# Optional
JAVA_TOOL_OPTIONS=-Xmx512m
```

### Frontend Environment Variables

Create `.env.production` in frontend root:

```bash
# API Configuration - Your backend URL
REACT_APP_API_BASE_URL=https://your-backend-url.com/api

# Encryption key (optional, but change from default)
REACT_APP_ENCRYPTION_KEY=your-unique-encryption-key
```

---

## 🎯 Backend Deployment

### Option 1: Railway.app (Recommended for Quick Setup)

1. **Push code to GitHub** (if not already)
   ```bash
   git add .
   git commit -m "Ready for deployment"
   git push origin main
   ```

2. **Deploy on Railway:**
   - Go to [railway.app](https://railway.app)
   - Click "New Project" → "Deploy from GitHub repo"
   - Select your repository
   - Railway will auto-detect Spring Boot

3. **Configure Environment Variables:**
   - In Railway dashboard, go to your service → Variables
   - Add all required environment variables listed above
   - **Important:** Set `SPRING_PROFILES_ACTIVE=prod`

4. **Add MySQL Database:**
   - In Railway, click "+ New" → "Database" → "Add MySQL"
   - Railway will provide connection details
   - Update your environment variables with Railway's DB credentials

5. **Configure Build:**
   - Railway will auto-detect Maven
   - Build command: `mvn clean package -DskipTests`
   - Start command: `java -jar target/InventoryMgtSystem-0.0.1-SNAPSHOT.jar`

6. **Get Your Backend URL:**
   - Railway will provide a URL like: `https://your-app.railway.app`
   - **Update CORS:** Go back to Variables and set:
     ```
     CORS_ALLOWED_ORIGINS=https://your-frontend-url.com
     ```

### Option 2: Render.com

1. **Create New Web Service:**
   - Go to [render.com](https://render.com)
   - Click "New" → "Web Service"
   - Connect your GitHub repository

2. **Configure Service:**
   - Name: inventory-backend
   - Environment: Java
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/InventoryMgtSystem-0.0.1-SNAPSHOT.jar`
   - Instance Type: Free or Starter

3. **Add Environment Variables:**
   - In Render dashboard, go to Environment
   - Add all required variables

4. **Add Database:**
   - Create new PostgreSQL database (or use MySQL via external service)
   - Get connection details and update DB_URL

### Option 3: Docker Deployment

1. **Create Dockerfile in backend folder:**
   ```dockerfile
   FROM openjdk:21-jdk-slim
   WORKDIR /app
   COPY target/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Build and Run:**
   ```bash
   cd backend
   mvn clean package -DskipTests
   docker build -t inventory-backend .
   docker run -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e DB_URL=your_db_url \
     -e DB_USERNAME=your_username \
     -e DB_PASSWORD=your_password \
     -e JWT_SECRET=your_jwt_secret \
     -e CORS_ALLOWED_ORIGINS=https://your-frontend.com \
     inventory-backend
   ```

---

## 🌐 Frontend Deployment

### Option 1: Vercel (Recommended)

1. **Update `.env.production`:**
   ```bash
   REACT_APP_API_BASE_URL=https://your-backend-url.railway.app/api
   REACT_APP_ENCRYPTION_KEY=your-encryption-key
   ```

2. **Deploy to Vercel:**
   ```bash
   npm install -g vercel
   vercel
   ```
   
   Or via Vercel dashboard:
   - Go to [vercel.com](https://vercel.com)
   - Import your GitHub repository
   - Vercel auto-detects React
   - Add environment variables in Vercel dashboard

3. **Configure Environment Variables in Vercel:**
   - Go to Project Settings → Environment Variables
   - Add `REACT_APP_API_BASE_URL` and `REACT_APP_ENCRYPTION_KEY`

4. **Get Your Frontend URL:**
   - Vercel provides: `https://your-app.vercel.app`
   - **Update Backend CORS:** Go back to Railway/Render and update `CORS_ALLOWED_ORIGINS`

### Option 2: Netlify

1. **Build Configuration:**
   - Build command: `npm run build`
   - Publish directory: `build`

2. **Deploy:**
   ```bash
   npm install -g netlify-cli
   netlify deploy --prod
   ```

3. **Environment Variables:**
   - Add in Netlify dashboard → Site settings → Environment variables

---

## 🗄️ Database Setup

### Create Database and Tables

1. **Connect to your production MySQL database**

2. **Create database:**
   ```sql
   CREATE DATABASE IF NOT EXISTS inventory_db;
   USE inventory_db;
   ```

3. **Initial deployment** (first time):
   - Temporarily set `spring.jpa.hibernate.ddl-auto=create` in your environment variables
   - Deploy backend - tables will be created automatically
   - **IMPORTANT:** Change back to `spring.jpa.hibernate.ddl-auto=validate` immediately

4. **Create admin user:**
   ```sql
   -- Generate BCrypt hash for your password at: https://bcrypt-generator.com
   INSERT INTO users (name, email, password, phone_number, role, created_at)
   VALUES (
       'Admin',
       'admin@yourdomain.com',
       '$2a$10$YourBCryptHashHere',
       '1234567890',
       'ADMIN',
       NOW()
   );
   ```

---

## ✅ Post-Deployment Verification

### 1. Test Backend Health

```bash
# Test if backend is running
curl https://your-backend-url.com/api/auth/login

# Should return: 400 Bad Request (expected, but shows API is up)
```

### 2. Test CORS

```bash
# From browser console on your frontend:
fetch('https://your-backend-url.com/api/products', {
  headers: {
    'Authorization': 'Bearer your-token'
  }
})
.then(res => res.json())
.then(console.log);
```

### 3. Test Frontend

1. Open your frontend URL
2. Try to login with admin credentials
3. Navigate through all pages
4. Test file upload
5. Check browser console for errors

### 4. Security Verification

- [ ] HTTPS is enabled (both frontend and backend)
- [ ] CORS only allows your frontend domain
- [ ] JWT tokens are being sent correctly
- [ ] File uploads work (max 5MB)
- [ ] Database connections use SSL

---

## 🔧 Common Issues & Solutions

### Issue 1: CORS Error

**Symptom:** "Access to fetch... has been blocked by CORS policy"

**Solution:**
- Verify `CORS_ALLOWED_ORIGINS` includes your exact frontend URL
- Include protocol: `https://` not just `yourdomain.com`
- For multiple domains: `https://domain1.com,https://domain2.com`

### Issue 2: 401 Unauthorized

**Symptom:** All API calls return 401 after login

**Solution:**
- Check JWT_SECRET is set correctly on backend
- Verify token is being stored in localStorage
- Check token expiration time

### Issue 3: Database Connection Failed

**Symptom:** Backend won't start, connection timeout

**Solution:**
- Verify DB_URL, DB_USERNAME, DB_PASSWORD
- Check if database allows external connections
- Enable SSL: `?useSSL=true&requireSSL=true`

### Issue 4: File Upload Fails

**Symptom:** "Error saving image"

**Solution:**
- Check file size (max 5MB)
- Verify file type (JPG, PNG, WEBP, GIF only)
- Ensure backend has write permissions

---

## 📝 Deployment Checklist Summary

### Before Going Live:

1. ✅ Generate strong JWT secret
2. ✅ Set up production database with SSL
3. ✅ Deploy backend with all environment variables
4. ✅ Deploy frontend with backend URL
5. ✅ Update CORS to frontend URL
6. ✅ Create admin user
7. ✅ Test all features
8. ✅ Verify HTTPS is working
9. ✅ Check security headers
10. ✅ Monitor logs for errors

---

## 🔗 Where to Update URLs During Deployment

| Component | File/Location | What to Change |
|-----------|---------------|----------------|
| Backend CORS | Railway/Render Environment Variables | `CORS_ALLOWED_ORIGINS=https://your-frontend.com` |
| Backend JWT | Railway/Render Environment Variables | `JWT_SECRET=your-generated-secret` |
| Backend DB | Railway/Render Environment Variables | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| Frontend API | `.env.production` | `REACT_APP_API_BASE_URL=https://your-backend.com/api` |
| Profile | Railway/Render Environment Variables | `SPRING_PROFILES_ACTIVE=prod` |

---

## 📞 Need Help?

- Check platform-specific documentation:
  - [Railway Docs](https://docs.railway.app)
  - [Render Docs](https://render.com/docs)
  - [Vercel Docs](https://vercel.com/docs)
  - [Netlify Docs](https://docs.netlify.com)

- Review logs in your deployment platform
- Test locally with `SPRING_PROFILES_ACTIVE=prod` first

---

**Remember:** Never commit secrets to Git. Always use environment variables!
