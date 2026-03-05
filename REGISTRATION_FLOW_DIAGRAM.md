# Registration Flow Diagram

## 📊 Complete Registration Flows

---

## 1️⃣ User Registration Flow (Auto-Approved)

```
┌─────────────────────────────────────────────────────────────────┐
│                    USER REGISTRATION FLOW                        │
└─────────────────────────────────────────────────────────────────┘

    User Opens Register Page
            │
            ▼
    ┌───────────────────────┐
    │  Fill Registration    │
    │  Form:                │
    │  • Username           │
    │  • Email              │
    │  • Password           │
    │  • Age (18+)          │
    │  • Country Code (+91) │
    │  • Phone (10 digits)  │
    │  • Gender             │
    │  • Role: USER ◄───────┼─── Select "User"
    └───────────────────────┘
            │
            ▼
    Click "Register" Button
            │
            ▼
    Frontend Validation
    ✓ All fields filled
    ✓ Passwords match
    ✓ Age >= 18
    ✓ Phone = 10 digits
            │
            ▼
    POST /api/auth/register/user
    {
      username, email, password,
      confirmPassword, age,
      countryCode: "+91",
      phoneNumber: "9876543210",
      gender
    }
            │
            ▼
    ┌───────────────────────┐
    │  Backend Processing   │
    │  • Validate data      │
    │  • Hash password      │
    │  • Set role = USER    │
    │  • Set status = APPROVED ◄─── Auto-approved!
    │  • Save to database   │
    └───────────────────────┘
            │
            ▼
    Success Response
    "User registered successfully"
            │
            ▼
    ┌───────────────────────┐
    │  Frontend Shows:      │
    │  "Registration        │
    │  successful! You can  │
    │  now login."          │
    └───────────────────────┘
            │
            ▼
    Auto-redirect to Login
    (after 3 seconds)
            │
            ▼
    User Logs In
            │
            ▼
    ✅ SUCCESS - Access Dashboard
```

---

## 2️⃣ Owner Registration Flow (Pending Approval)

```
┌─────────────────────────────────────────────────────────────────┐
│                   OWNER REGISTRATION FLOW                        │
└─────────────────────────────────────────────────────────────────┘

    Owner Opens Register Page
            │
            ▼
    ┌───────────────────────┐
    │  Fill Registration    │
    │  Form:                │
    │  • Username           │
    │  • Email              │
    │  • Password           │
    │  • Age (18+)          │
    │  • Country Code (+1)  │
    │  • Phone (10 digits)  │
    │  • Gender             │
    │  • Role: OWNER ◄──────┼─── Select "Owner (Requires Admin Approval)"
    └───────────────────────┘
            │
            ▼
    ⚠️ Info Alert Shows:
    "Owner accounts require
    admin approval before
    you can login."
            │
            ▼
    Click "Register" Button
            │
            ▼
    Frontend Validation
    ✓ All fields filled
    ✓ Passwords match
    ✓ Age >= 18
    ✓ Phone = 10 digits
            │
            ▼
    POST /api/auth/register/owner
    {
      username, email, password,
      confirmPassword, age,
      countryCode: "+1",
      phoneNumber: "5551234567",
      gender
    }
            │
            ▼
    ┌───────────────────────┐
    │  Backend Processing   │
    │  • Validate data      │
    │  • Hash password      │
    │  • Set role = OWNER   │
    │  • Set status = PENDING ◄─── Requires approval!
    │  • Set autoApprove = false
    │  • Save to database   │
    │  • Send email to admin│
    └───────────────────────┘
            │
            ▼
    Success Response
    "Owner registered successfully"
            │
            ▼
    ┌───────────────────────┐
    │  Frontend Shows:      │
    │  "Registration        │
    │  successful! Your     │
    │  account is pending   │
    │  admin approval. You  │
    │  will receive an email│
    │  once approved."      │
    └───────────────────────┘
            │
            ▼
    Auto-redirect to Login
    (after 3 seconds)
            │
            ▼
    Owner Tries to Login
            │
            ▼
    ❌ Login Fails
    "Account pending approval"
            │
            │
            ├─────────────────────────────────────┐
            │                                     │
            ▼                                     ▼
    Owner Waits                          Admin Logs In
                                                │
                                                ▼
                                    ┌───────────────────────┐
                                    │  Admin Dashboard      │
                                    │  • Pending Approvals  │
                                    │    Tab shows owner    │
                                    │  • Click "Approve"    │
                                    └───────────────────────┘
                                                │
                                                ▼
                                    POST /api/admin/approve/{userId}
                                                │
                                                ▼
                                    ┌───────────────────────┐
                                    │  Backend Updates:     │
                                    │  • status = APPROVED  │
                                    │  • Send approval email│
                                    └───────────────────────┘
                                                │
                                                ▼
                                    Owner Receives Email
                                                │
            ┌───────────────────────────────────┘
            │
            ▼
    Owner Logs In Again
            │
            ▼
    ✅ SUCCESS - Access Dashboard
```

---

## 3️⃣ Admin Registration Flow (Admin Only)

```
┌─────────────────────────────────────────────────────────────────┐
│                   ADMIN REGISTRATION FLOW                        │
└─────────────────────────────────────────────────────────────────┘

    ❌ Public Cannot Access
    (No admin registration
     on public register page)
            │
            │
            ▼
    Existing Admin Logs In
    (username: admin, password: admin123)
            │
            ▼
    ┌───────────────────────┐
    │  Admin Dashboard      │
    │  • Header shows       │
    │    "Create Admin"     │
    │    button             │
    └───────────────────────┘
            │
            ▼
    Click "Create Admin" Button
            │
            ▼
    ┌───────────────────────┐
    │  Dialog Opens with    │
    │  Admin Creation Form: │
    │  • Username           │
    │  • Email              │
    │  • Password           │
    │  • Confirm Password   │
    │  • Age (18+)          │
    │  • Country Code (+44) │
    │  • Phone (10 digits)  │
    │  • Gender             │
    └───────────────────────┘
            │
            ▼
    Fill Form and Click
    "Create Admin"
            │
            ▼
    Frontend Validation
    ✓ All fields filled
    ✓ Passwords match
    ✓ Age >= 18
    ✓ Phone = 10 digits
            │
            ▼
    POST /api/auth/register/admin
    Headers: {
      Authorization: "Bearer <JWT_TOKEN>" ◄─── Admin token required!
    }
    Body: {
      username, email, password,
      confirmPassword, age,
      countryCode: "+44",
      phoneNumber: "7700900123",
      gender
    }
            │
            ▼
    ┌───────────────────────┐
    │  Backend Security     │
    │  Check:               │
    │  @PreAuthorize(       │
    │   "hasAuthority(      │
    │    'ADMIN')"          │
    │  )                    │
    │  ✓ Verify JWT token   │
    │  ✓ Check ADMIN role   │
    └───────────────────────┘
            │
            ├─── If NOT admin ──► ❌ 401 Unauthorized
            │
            ▼ If admin
    ┌───────────────────────┐
    │  Backend Processing   │
    │  • Validate data      │
    │  • Hash password      │
    │  • Set role = ADMIN   │
    │  • Set status = APPROVED ◄─── Auto-approved!
    │  • Save to database   │
    │  • Send welcome email │
    └───────────────────────┘
            │
            ▼
    Success Response
    "Admin registered successfully"
            │
            ▼
    ┌───────────────────────┐
    │  Frontend Shows:      │
    │  "Admin account       │
    │  created successfully!"│
    │  • Dialog closes      │
    │  • User list refreshes│
    │  • New admin appears  │
    │    in "All Users" tab │
    └───────────────────────┘
            │
            ▼
    New Admin Can Login
            │
            ▼
    ✅ SUCCESS - Full Admin Access
```

---

## 🔐 Security Comparison

```
┌─────────────────────────────────────────────────────────────────┐
│                      ENDPOINT SECURITY                           │
└─────────────────────────────────────────────────────────────────┘

POST /api/auth/register/user
├─ Access: PUBLIC ✅
├─ Authentication: NOT REQUIRED
├─ Authorization: NONE
├─ Status: APPROVED (auto)
└─ Can login: IMMEDIATELY

POST /api/auth/register/owner
├─ Access: PUBLIC ✅
├─ Authentication: NOT REQUIRED
├─ Authorization: NONE
├─ Status: PENDING (requires approval)
└─ Can login: AFTER ADMIN APPROVAL

POST /api/auth/register/admin
├─ Access: PROTECTED 🔒
├─ Authentication: REQUIRED (JWT token)
├─ Authorization: ADMIN role only
├─ Status: APPROVED (auto)
└─ Can login: IMMEDIATELY
```

---

## 📊 Status Lifecycle

```
┌─────────────────────────────────────────────────────────────────┐
│                      USER STATUS LIFECYCLE                       │
└─────────────────────────────────────────────────────────────────┘

USER Registration:
    Register ──► APPROVED ──► Can Login ✅

OWNER Registration:
    Register ──► PENDING ──► Cannot Login ❌
                    │
                    ▼ Admin Approves
                 APPROVED ──► Can Login ✅
                    │
                    ▼ Admin Rejects
                 REJECTED ──► Cannot Login ❌

ADMIN Registration:
    Register ──► APPROVED ──► Can Login ✅
    (Only by existing admin)
```

---

## 🌍 Country Code Integration

```
┌─────────────────────────────────────────────────────────────────┐
│                   COUNTRY CODE FLOW                              │
└─────────────────────────────────────────────────────────────────┘

    User Selects Country Code
            │
            ▼
    ┌───────────────────────┐
    │  Dropdown Options:    │
    │  +1   (USA/Canada)    │
    │  +44  (UK)            │
    │  +91  (India)         │
    │  +86  (China)         │
    │  +81  (Japan)         │
    │  ... 15 more          │
    └───────────────────────┘
            │
            ▼
    User Enters Phone Number
    (10 digits only)
            │
            ▼
    Preview Shows:
    "+91 9876543210"
            │
            ▼
    Submit Registration
            │
            ▼
    Backend Receives:
    {
      countryCode: "+91",
      phoneNumber: "9876543210"
    }
            │
            ▼
    Backend Validates:
    ✓ countryCode matches: ^\\+[0-9]{1,4}$
    ✓ phoneNumber matches: ^[0-9]{10}$
            │
            ▼
    Save to Database:
    ┌─────────────┬──────────────┐
    │ country_code│ phone_number │
    ├─────────────┼──────────────┤
    │ +91         │ 9876543210   │
    └─────────────┴──────────────┘
            │
            ▼
    Display in Admin Dashboard:
    "+91 9876543210"
```

---

## 📋 Complete Feature Matrix

```
┌─────────────────────────────────────────────────────────────────┐
│                      FEATURE MATRIX                              │
└─────────────────────────────────────────────────────────────────┘

Feature                 │ USER  │ OWNER │ ADMIN
────────────────────────┼───────┼───────┼──────
Self-Register           │  ✅   │  ✅   │  ❌
Public Endpoint         │  ✅   │  ✅   │  ❌
Requires Admin Login    │  ❌   │  ❌   │  ✅
Auto-Approved           │  ✅   │  ❌   │  ✅
Requires Approval       │  ❌   │  ✅   │  ❌
Can Login Immediately   │  ✅   │  ❌   │  ✅
Country Code Required   │  ✅   │  ✅   │  ✅
Email Notification      │  ✅   │  ✅   │  ✅
Admin Notification      │  ❌   │  ✅   │  ❌
```

---

## 🎯 Quick Reference

### User Registration
- **Endpoint:** POST /api/auth/register/user
- **Access:** Public
- **Status:** APPROVED
- **Login:** Immediate

### Owner Registration
- **Endpoint:** POST /api/auth/register/owner
- **Access:** Public
- **Status:** PENDING
- **Login:** After approval

### Admin Registration
- **Endpoint:** POST /api/auth/register/admin
- **Access:** Admin only
- **Status:** APPROVED
- **Login:** Immediate

---

**All flows implemented and ready to use!** 🚀
