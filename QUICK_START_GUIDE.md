# Quick Start Guide - New Features

## 🚀 3-Step Setup

### Step 1: Apply Frontend Updates
```bash
cd authorization-service
update-frontend.bat
```
**What it does:** Copies updated React components to auth-frontend folder

### Step 2: Restart Backend
```bash
cd authorization-service
./mvnw spring-boot:run
```
**What it does:** Creates `country_code` database column

### Step 3: Restart Frontend
```bash
cd auth-frontend
npm start
```
**What it does:** Loads new components with country code support

---

## ✅ Quick Test

### Test User Registration (Auto-Approved)
1. Go to http://localhost:3000/register
2. Fill form, select country code (+91), select role "User"
3. Click Register → Should show "You can now login"
4. Login immediately → Should work ✅

### Test Owner Registration (Pending Approval)
1. Go to http://localhost:3000/register
2. Fill form, select country code (+1), select role "Owner"
3. Click Register → Should show "Pending admin approval"
4. Try to login → Should fail (pending)
5. Login as admin → Go to dashboard → Approve owner
6. Login as owner → Should work ✅

### Test Admin Creation (Admin Only)
1. Login as admin (username: admin, password: admin123)
2. Go to Admin Dashboard
3. Click "Create Admin" button
4. Fill form with country code
5. Click Create → Should show success
6. Logout and login with new admin → Should work ✅

---

## 📋 What Changed

| Feature | Status |
|---------|--------|
| Country code for phone | ✅ Ready |
| User registration (auto-approved) | ✅ Ready |
| Owner registration (pending) | ✅ Ready |
| Admin registration (secured) | ✅ Ready |
| Admin creation UI | ✅ Ready |

---

## 🔧 Files Updated

### Backend (Already Done ✅)
- AuthController.java
- RegisterRequest.java
- UserCredential.java
- AuthServiceImpl.java

### Frontend (Need to Apply 📋)
- Register.js → `frontend-updates/Register.js`
- AdminDashboard.js → `frontend-updates/AdminDashboard.js`
- api.js → `frontend-updates/api.js`

---

## 📞 URLs

- Frontend: http://localhost:3000
- Backend API: http://localhost:8081/api
- Swagger Docs: http://localhost:8081/swagger-ui.html

---

## 🎯 Default Admin Credentials

- Username: `admin`
- Password: `admin123`

---

## 📚 Full Documentation

- `COMPLETE_UPDATE_SUMMARY.md` - Complete details
- `FRONTEND_UPDATE_INSTRUCTIONS.md` - Step-by-step guide
- `FRONTEND_BACKEND_ALIGNMENT.md` - Verification report
- `NEW_FEATURES_IMPLEMENTED.md` - Feature summary

---

**Ready to go!** Run `update-frontend.bat` and restart both servers. 🚀
