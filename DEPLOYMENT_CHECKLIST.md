# ========================================
# DEPLOYMENT CHECKLIST
# ========================================

## 1️⃣ RAILWAY DATABASE (Already Done ✓)
- [✓] Created MySQL database: inventory-db
- [ ] Get connection credentials:
  - Railway Dashboard → inventory-db → Connect
  - Copy: MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER, MYSQLPASSWORD

## 2️⃣ GENERATE JWT SECRET
Run this command in PowerShell:
```powershell
$bytes = New-Object byte[] 32; (New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($bytes); [Convert]::ToBase64String($bytes)
```
OR in Git Bash/Linux:
```bash
openssl rand -base64 32
```
Save the output - you'll need it for Render environment variables.

## 3️⃣ DEPLOY BACKEND ON RENDER.COM
1. Go to: https://render.com
2. Click "New +" → "Web Service"
3. Connect GitHub: lal5hivam/Inventory-Mgmt
4. Configure:
   - Name: inventory-backend
   - Root Directory: backend
   - Environment: Docker
   - Instance Type: Free
   (Dockerfile will handle build automatically)

5. Set Environment Variables (see backend/.env.render.example):
   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_URL=jdbc:mysql://RAILWAY_HOST:PORT/railway?useSSL=true&requireSSL=true&serverTimezone=UTC
   DB_USERNAME=root
   DB_PASSWORD=RAILWAY_DB_PASSWORD
   JWT_SECRET=<generated_secret_from_step_2>
   CORS_ALLOWED_ORIGINS=https://your-frontend.railway.app
   ```

6. Click "Create Web Service"
7. Wait for deployment (~5-10 minutes for first build)
8. Copy your backend URL (e.g., https://inventory-backend.onrender.com)

## 4️⃣ CREATE DATABASE TABLES
On first deployment with SPRING_PROFILES_ACTIVE=prod:
- Tables are NOT auto-created (ddl-auto=validate)
- You need to create them manually

**Option A: Temporary auto-create** (Easiest)
1. Change Render env: SPRING_PROFILES_ACTIVE=dev
2. Redeploy (tables will be created)
3. Change back to: SPRING_PROFILES_ACTIVE=prod
4. Redeploy

**Option B: Run SQL manually** in Railway Data tab:
```sql
-- Run the SQL schema from backend/src/main/resources/schema.sql
-- (If you have one, or let Hibernate generate it first with ddl-auto=create)
```

## 5️⃣ CREATE ADMIN USER
After tables exist, run this in Railway → inventory-db → Data/Query:
```sql
INSERT INTO users (name, email, password, phone_number, role) 
VALUES (
  'Admin User',
  'admin@admin.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeJ8bFjJfJ8qzJNfvFYZKCqJ8Xf.Fw2',
  '1234567890',
  'ADMIN'
);
```
Login credentials:
- Email: admin@admin.com
- Password: password123

## 6️⃣ UPDATE FRONTEND ON RAILWAY
1. Go to Railway Dashboard → Your frontend service
2. Click "Variables" tab
3. Add new variable:
   - Key: REACT_APP_API_BASE_URL
   - Value: https://inventory-backend.onrender.com (your Render URL)
4. Redeploy the service

## 7️⃣ UPDATE CORS ON BACKEND
1. Get your Railway frontend URL (e.g., https://inventory-mgmt-production.up.railway.app)
2. Go to Render → Your backend service → Environment
3. Update CORS_ALLOWED_ORIGINS to your frontend URL
4. Redeploy

## 8️⃣ TEST THE APPLICATION
1. Open your Railway frontend URL
2. Try to login with admin@admin.com / password123
3. Check browser console for errors
4. Verify API calls are working

## 🔧 TROUBLESHOOTING

**Backend won't start:**
- Check Render logs for errors
- Verify all environment variables are set
- Ensure Railway database is accessible (use public URL if needed)

**Frontend can't connect:**
- Check CORS error in browser console
- Verify REACT_APP_API_BASE_URL is correct
- Ensure CORS_ALLOWED_ORIGINS matches frontend URL exactly

**Database connection failed:**
- Verify Railway database credentials
- Check if Railway database accepts external connections
- Try using Railway's public network URL

**401 Unauthorized:**
- JWT_SECRET must be at least 256 bits (32+ characters)
- Ensure admin user was created successfully
- Check password hash is correct

## 📝 IMPORTANT NOTES

1. **Render Free Tier**: Spins down after 15 min inactivity, first request takes ~30s
2. **Railway Free Tier**: $5 credit/month, monitor usage
3. **Database Backup**: Set up Railway database backups
4. **Security**: Never commit .env files with real credentials
5. **HTTPS**: Both Railway and Render provide HTTPS automatically

## ✅ DEPLOYMENT COMPLETE
Once all steps are done:
- ✅ Backend running on Render
- ✅ Frontend running on Railway  
- ✅ Database on Railway MySQL
- ✅ CORS configured correctly
- ✅ Admin user created
- ✅ Application accessible via HTTPS
