# Project Folder Structure

## ✅ Frontend Moved Successfully!

The frontend has been moved outside the `authorization-service` folder as requested.

## 📁 New Structure

```
auth/                           (Parent folder)
│
├── authorization-service/      (Backend - Spring Boot)
│   ├── src/
│   │   ├── main/java/         # Java source code
│   │   ├── main/resources/    # Configuration files
│   │   └── test/              # Test files
│   ├── target/                # Build output
│   ├── pom.xml                # Maven configuration
│   ├── start-all.bat          # Startup script (starts both!)
│   ├── .env.example           # Environment template
│   ├── docker-compose.yml     # Docker setup
│   │
│   └── Documentation/
│       ├── README.md
│       ├── COMPLETE_SYSTEM_GUIDE.md
│       ├── FRONTEND_SETUP.md
│       ├── PROJECT_COMPLETE.md
│       ├── SYSTEM_ARCHITECTURE.md
│       ├── DEPLOYMENT_CHECKLIST.md
│       ├── TEST_RESULTS.md
│       └── More...
│
└── auth-frontend/             (Frontend - React)
    ├── src/
    │   ├── components/        # React components
    │   │   ├── Login.js
    │   │   ├── Register.js
    │   │   ├── ForgotPassword.js
    │   │   ├── Dashboard.js
    │   │   ├── AdminDashboard.js
    │   │   └── PrivateRoute.js
    │   ├── context/           # State management
    │   │   └── AuthContext.js
    │   ├── services/          # API integration
    │   │   └── api.js
    │   ├── App.js             # Main app
    │   ├── index.js           # Entry point
    │   └── index.css          # Global styles
    ├── public/                # Static files
    ├── node_modules/          # Dependencies
    ├── package.json           # NPM configuration
    ├── .gitignore
    │
    └── Documentation/
        ├── README.md
        ├── QUICK_START.md
        └── UI_PREVIEW.md
```

## 🚀 How to Start

### Option 1: Use Startup Script (Easiest)
```bash
# Navigate to backend folder
cd authorization-service

# Run the startup script
start-all.bat
```

The script will:
1. Start the backend server (Spring Boot)
2. Wait 10 seconds
3. Start the frontend server (React)

### Option 2: Manual Start

**Terminal 1 - Backend:**
```bash
cd authorization-service
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd auth-frontend
npm install  # First time only
npm start
```

## 🌐 Access Points

Once both servers are running:

- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8081/api
- **API Docs**: http://localhost:8081/swagger-ui.html

## 📝 Important Notes

### Startup Script Location
The `start-all.bat` script is located in the `authorization-service` folder and will automatically start both backend and frontend from their respective locations.

### Path Updates
All documentation has been updated to reflect the new folder structure:
- ✅ start-all.bat - Updated paths
- ✅ COMPLETE_SYSTEM_GUIDE.md - Updated structure
- ✅ PROJECT_COMPLETE.md - Updated paths
- ✅ README.md - Updated instructions

### Git Repositories
You can now have separate Git repositories:

**Option 1: Separate Repos**
```bash
# Backend repo
cd authorization-service
git init
git add .
git commit -m "Initial backend commit"

# Frontend repo
cd ../auth-frontend
git init
git add .
git commit -m "Initial frontend commit"
```

**Option 2: Monorepo**
```bash
# Single repo for both
cd auth  # Parent folder
git init
git add .
git commit -m "Initial commit - full stack"
```

### Development Workflow

**Working on Backend:**
```bash
cd authorization-service
# Make changes
./mvnw test
./mvnw spring-boot:run
```

**Working on Frontend:**
```bash
cd auth-frontend
# Make changes
npm start
# Test in browser
```

**Working on Both:**
```bash
# Terminal 1
cd authorization-service
./mvnw spring-boot:run

# Terminal 2
cd auth-frontend
npm start
```

## 🔧 Configuration

### Backend Configuration
Location: `authorization-service/.env`
```env
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_jwt_secret
SMTP_USERNAME=your_email
SMTP_PASSWORD=your_password
```

### Frontend Configuration
Location: `auth-frontend/src/services/api.js`
```javascript
const API_BASE_URL = 'http://localhost:8081/api';
```

## 📦 Building for Production

### Backend
```bash
cd authorization-service
./mvnw clean package
# Output: target/authorization-service-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd auth-frontend
npm run build
# Output: build/ folder
```

## 🚀 Deployment

### Option 1: Integrated (Serve frontend from backend)
```bash
# Build frontend
cd auth-frontend
npm run build

# Copy to backend static folder
cp -r build/* ../authorization-service/src/main/resources/static/

# Build backend
cd ../authorization-service
./mvnw clean package

# Deploy single JAR file
java -jar target/authorization-service-0.0.1-SNAPSHOT.jar
```

### Option 2: Separated (Deploy independently)
```bash
# Deploy frontend to Netlify/Vercel
cd auth-frontend
npm run build
netlify deploy --prod

# Deploy backend to AWS/Heroku
cd ../authorization-service
./mvnw clean package
# Deploy JAR to cloud provider
```

## ✅ Benefits of Separate Folders

1. **Clear Separation**: Backend and frontend are clearly separated
2. **Independent Development**: Work on each independently
3. **Separate Git Repos**: Can have separate version control
4. **Easier Deployment**: Deploy each separately if needed
5. **Team Collaboration**: Different teams can work on each
6. **Cleaner Structure**: No mixing of Java and JavaScript files

## 📚 Documentation Locations

### Backend Documentation
Location: `authorization-service/`
- README.md - Main backend readme
- COMPLETE_SYSTEM_GUIDE.md - Full system guide
- FRONTEND_SETUP.md - Frontend setup details
- SYSTEM_ARCHITECTURE.md - Architecture diagrams
- DEPLOYMENT_CHECKLIST.md - Deployment guide
- TEST_RESULTS.md - Test results
- PROJECT_COMPLETE.md - Project summary

### Frontend Documentation
Location: `auth-frontend/`
- README.md - Frontend readme
- QUICK_START.md - Quick start guide
- UI_PREVIEW.md - UI preview

## 🎉 Summary

✅ Frontend successfully moved to `../auth-frontend`
✅ Backend remains in `authorization-service`
✅ Startup script updated to work with new structure
✅ All documentation updated
✅ Both can be started with one command
✅ Clear separation of concerns
✅ Ready for development and deployment

## 🚀 Next Steps

1. Navigate to `authorization-service` folder
2. Run `start-all.bat`
3. Open http://localhost:3000
4. Enjoy your full-stack authentication system!

---

**The project structure is now optimized for development and deployment!** 🎊
