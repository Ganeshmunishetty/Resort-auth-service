# Frontend Update Instructions

## 🎯 Overview

The backend has been updated with new features:
1. ✅ Country code support for phone numbers
2. ✅ Admin registration (secured - only accessible by logged-in admins)
3. ✅ Owner registration requires admin approval (PENDING status)
4. ✅ User registration auto-approved (existing behavior)

Now you need to update the frontend files to match these changes.

---

## 📁 Files to Update

The updated files are located in the `authorization-service/frontend-updates/` folder.

You need to copy these files to your `auth-frontend` folder:

```
authorization-service/frontend-updates/
├── Register.js          → Copy to: auth-frontend/src/components/Register.js
├── AdminDashboard.js    → Copy to: auth-frontend/src/components/AdminDashboard.js
└── api.js               → Copy to: auth-frontend/src/services/api.js
```

---

## 🔄 Step-by-Step Update Process

### Step 1: Stop the Frontend Server
If your frontend is running, stop it first (Ctrl+C in the terminal).

### Step 2: Backup Current Files (Optional)
```bash
cd auth-frontend/src/components
copy Register.js Register.js.backup
copy AdminDashboard.js AdminDashboard.js.backup

cd ../services
copy api.js api.js.backup
```

### Step 3: Copy Updated Files

**Option A: Manual Copy (Windows)**
1. Open File Explorer
2. Navigate to `authorization-service/frontend-updates/`
3. Copy `Register.js` to `auth-frontend/src/components/Register.js`
4. Copy `AdminDashboard.js` to `auth-frontend/src/components/AdminDashboard.js`
5. Copy `api.js` to `auth-frontend/src/services/api.js`

**Option B: Command Line (Windows)**
```bash
# From authorization-service folder
cd ..
copy authorization-service\frontend-updates\Register.js auth-frontend\src\components\Register.js
copy authorization-service\frontend-updates\AdminDashboard.js auth-frontend\src\components\AdminDashboard.js
copy authorization-service\frontend-updates\api.js auth-frontend\src\services\api.js
```

### Step 4: Restart Frontend
```bash
cd auth-frontend
npm start
```

---

## ✨ What's New in Each File

### 1. Register.js
**New Features:**
- ✅ Country code dropdown selector (20 countries)
- ✅ Phone number field with country code preview
- ✅ Role selector: "User" or "Owner (Requires Admin Approval)"
- ✅ Info alert when selecting Owner role
- ✅ Different success messages for User vs Owner registration
- ✅ Form validation for all fields including country code
- ✅ Auto-redirect to login after 3 seconds

**Country Codes Included:**
- +1 (USA/Canada)
- +44 (UK)
- +91 (India)
- +86 (China)
- +81 (Japan)
- +49 (Germany)
- +33 (France)
- +61 (Australia)
- +971 (UAE)
- +65 (Singapore)
- And 10 more...

### 2. AdminDashboard.js
**New Features:**
- ✅ "Create Admin" button in header
- ✅ Admin creation dialog with full form
- ✅ Country code selector in admin form
- ✅ Phone number display with country code (e.g., "+91 9876543210")
- ✅ Two tabs: "Pending Approvals" and "All Users"
- ✅ Approve/Reject buttons for pending users
- ✅ Unlock button for locked accounts
- ✅ Form validation for admin creation
- ✅ Success/error messages

### 3. api.js
**New Features:**
- ✅ `registerAdmin()` function - calls `/auth/register/admin` endpoint
- ✅ Automatically includes JWT token in admin registration request
- ✅ Updated `register()` function to handle USER and OWNER roles
- ✅ All admin endpoints included

---

## 🧪 Testing the Updates

### Test 1: User Registration
1. Go to http://localhost:3000/register
2. Fill all fields
3. Select country code (e.g., +91)
4. Enter 10-digit phone number
5. Select "User" role
6. Click Register
7. ✅ Should show: "Registration successful! You can now login."
8. ✅ Should redirect to login after 3 seconds
9. ✅ Login should work immediately

### Test 2: Owner Registration
1. Go to http://localhost:3000/register
2. Fill all fields
3. Select country code (e.g., +1)
4. Enter 10-digit phone number
5. Select "Owner (Requires Admin Approval)" role
6. ✅ Should show info alert: "Owner accounts require admin approval"
7. Click Register
8. ✅ Should show: "Registration successful! Your account is pending admin approval..."
9. ✅ Login should fail with "pending approval" message
10. Login as admin
11. Go to Admin Dashboard
12. ✅ Should see owner in "Pending Approvals" tab
13. Click Approve
14. ✅ Owner can now login

### Test 3: Admin Registration
1. Try to access admin registration without login
2. ✅ Should get 401 Unauthorized error
3. Login as admin
4. Go to Admin Dashboard
5. Click "Create Admin" button
6. ✅ Dialog should open with form
7. Fill all fields including country code
8. Click "Create Admin"
9. ✅ Should show success message
10. ✅ New admin should appear in "All Users" tab
11. Logout and login with new admin credentials
12. ✅ Should work

### Test 4: Country Code Display
1. Login as admin
2. Go to Admin Dashboard
3. Check "All Users" tab
4. ✅ Phone numbers should display as: "+91 9876543210"
5. ✅ Country code should be separate from phone number

---

## 🐛 Troubleshooting

### Issue: "Country code is required" error
**Solution:** Make sure you selected a country code from the dropdown.

### Issue: "Phone must be 10 digits" error
**Solution:** Enter exactly 10 digits without spaces or dashes.

### Issue: Admin registration returns 401
**Solution:** 
1. Make sure you're logged in as admin
2. Check that JWT token is in localStorage
3. Verify token hasn't expired (login again)

### Issue: Owner registration not showing in pending
**Solution:**
1. Check backend logs for errors
2. Verify database connection
3. Make sure backend is running on port 8081

### Issue: Country codes not showing
**Solution:**
1. Clear browser cache
2. Hard refresh (Ctrl+Shift+R)
3. Check browser console for errors

---

## 📊 Backend Compatibility

These frontend updates are compatible with the backend changes already made:

✅ Backend has `countryCode` field in `RegisterRequest.java`
✅ Backend has `countryCode` column in `UserCredential` entity
✅ Backend has admin registration endpoint secured with `@PreAuthorize`
✅ Backend sets PENDING status for owner registration
✅ Backend sets APPROVED status for user registration

---

## 🔄 Database Migration

The backend will automatically create the `country_code` column when you restart it.

**To restart backend:**
```bash
cd authorization-service
# Stop current server (Ctrl+C)
./mvnw spring-boot:run
```

Check logs for:
```
Hibernate: alter table user_credentials add column country_code varchar(10)
```

---

## 📝 Summary of Changes

| Feature | Before | After |
|---------|--------|-------|
| Phone Number | Single field | Country code + Phone number |
| User Registration | Auto-approved | Auto-approved (no change) |
| Owner Registration | Auto-approved | Requires admin approval |
| Admin Registration | Public endpoint | Protected (admin only) |
| Admin Creation | Not available | Available in admin dashboard |
| Phone Display | 9876543210 | +91 9876543210 |

---

## ✅ Verification Checklist

After updating files:

- [ ] Files copied to correct locations
- [ ] Frontend server restarted
- [ ] No console errors in browser
- [ ] Register page shows country code dropdown
- [ ] Register page shows role selector
- [ ] Admin dashboard shows "Create Admin" button
- [ ] Phone numbers display with country code
- [ ] User registration works (auto-approved)
- [ ] Owner registration works (pending approval)
- [ ] Admin registration works (from admin dashboard)
- [ ] All validation messages work correctly

---

## 🎉 Next Steps

Once you've updated the frontend:

1. Test all three registration flows
2. Verify country codes are saved correctly
3. Check admin approval workflow
4. Test admin creation from dashboard
5. Verify phone number display format

---

## 📞 Support

If you encounter any issues:

1. Check browser console for errors
2. Check backend logs for API errors
3. Verify all files are in correct locations
4. Make sure backend is running on port 8081
5. Clear browser cache and try again

---

**All frontend updates are ready to deploy!** 🚀

Just copy the three files and restart your frontend server.
