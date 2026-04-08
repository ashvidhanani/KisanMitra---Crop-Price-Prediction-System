#  KisanMitra — Crop Price Prediction System
 
> A full-stack Decision Support System that predicts Indian crop market prices using Machine Learning, with user authentication and personalized prediction history.
 
---
 
## 📌 Table of Contents
 
- [Project Overview](#-project-overview)
- [Tech Stack](#-tech-stack)
- [System Architecture](#-system-architecture)
- [Project Structure](#-project-structure)
- [Features](#-features)
- [Prerequisites](#-prerequisites)
- [Setup & Installation](#-setup--installation)
  - [1. Firebase Setup](#1-firebase-setup)
  - [2. AI Service (Python / Flask)](#2-ai-service-python--flask)
  - [3. Backend (Spring Boot)](#3-backend-spring-boot)
  - [4. Frontend (Angular)](#4-frontend-angular)
- [Running the Application](#-running-the-application)
- [API Reference](#-api-reference)
- [ML Model Details](#-ml-model-details)
- [Firebase Firestore Schema](#-firebase-firestore-schema)
- [Data Flow](#-data-flow)
- [Future Enhancements](#-future-enhancements)
- [Author](#-author)
 
---
 
## 📖 Project Overview
 
KisanMitra is a final-year Computer Engineering project that helps **farmers, traders, and agricultural planners** make informed decisions by predicting crop prices based on:
 
- Crop type
- Market location
- Target date
 
The system uses a trained **RandomForestRegressor** model (scikit-learn) served via a **Flask REST API**, integrated with a **Spring Boot** backend and an **Angular** frontend. All predictions are stored per-user in **Firebase Firestore**.
 
---
 
## 🛠 Tech Stack
 
| Layer | Technology |
|---|---|
| Frontend | Angular 17 (Standalone Components) |
| Backend | Java 17 + Spring Boot 3.3 |
| AI Service | Python 3.10 + Flask |
| ML Libraries | Pandas, NumPy, Scikit-learn |
| Database | Firebase Firestore (Cloud) |
| Auth | In-memory (Spring Boot) + localStorage (Angular) |
| Build Tools | Maven, Angular CLI, pip |
 
---
 
## 🏗 System Architecture
 
```
┌─────────────────────────────────────────────────────┐
│               Angular Frontend :4200                │
│   /login  /register  /predict  /history             │
└───────────────────┬─────────────────────────────────┘
                    │ HTTP REST
                    ▼
┌─────────────────────────────────────────────────────┐
│            Spring Boot Backend :8080                │
│   POST /api/auth/register                           │
│   POST /api/auth/login                              │
│   POST /api/predictPrice                            │
│   GET  /api/history?userId=...                      │
└──────────┬──────────────────────┬───────────────────┘
           │ HTTP REST            │ Firestore SDK
           ▼                      ▼
┌─────────────────┐   ┌───────────────────────────────┐
│  Flask AI :5000 │   │     Firebase Firestore        │
│  POST /predict  │   │   Collection: "predictions"   │
│                 │   │   (user-linked documents)     │
│  RandomForest   │   └───────────────────────────────┘
│  .pkl model     │
└─────────────────┘
```
 
---
 
## 📁 Project Structure
 
```
crop-price-prediction/
│
├── README.md
│
├── dataset/
│   └── crop_prices.csv                    ← Historical price data (INR/quintal)
│
├── ai-service/                            ← Python Flask + ML
│   ├── app.py                             ← Flask REST API server
│   ├── train_model.py                     ← Model training script
│   ├── requirements.txt
│   └── models/                            ← Auto-generated after training
│       ├── crop_price_model.pkl
│       └── encoders.pkl
│
├── backend/                               ← Java Spring Boot
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/croppredict/
│       │   ├── CropPriceApplication.java
│       │   ├── controller/
│       │   │   ├── AuthController.java    ← /api/auth/*
│       │   │   └── PredictionController.java
│       │   ├── service/
│       │   │   ├── AuthService.java       ← In-memory user store
│       │   │   └── PredictionService.java ← Flask caller + Firestore
│       │   ├── model/
│       │   │   ├── User.java
│       │   │   ├── AuthRequest.java
│       │   │   ├── AuthResponse.java
│       │   │   ├── PredictionRequest.java ← includes userId
│       │   │   ├── PredictionResponse.java
│       │   │   └── PredictionHistory.java
│       │   └── config/
│       │       ├── FirebaseConfig.java
│       │       └── CorsConfig.java
│       └── resources/
│           ├── application.properties
│           └── firebase-service-account.json  ← YOU ADD THIS
│
└── frontend/                              ← Angular 17
    ├── angular.json
    ├── package.json
    ├── tsconfig.json
    └── src/
        ├── index.html
        ├── main.ts
        ├── styles.css
        ├── environments/
        │   └── environment.ts
        └── app/
            ├── app.component.ts
            ├── app.config.ts
            ├── app.routes.ts
            ├── models/
            │   └── prediction.model.ts
            ├── services/
            │   ├── auth.service.ts
            │   └── prediction.service.ts
            └── components/
                ├── navbar/
                │   ├── navbar.component.ts
                │   ├── navbar.component.html
                │   └── navbar.component.css
                ├── auth/
                │   ├── login/
                │   │   ├── login.component.ts
                │   │   ├── login.component.html
                │   │   └── login.component.css
                │   └── register/
                │       ├── register.component.ts
                │       ├── register.component.html
                │       └── register.component.css
                ├── predict/
                │   ├── predict.component.ts
                │   ├── predict.component.html
                │   └── predict.component.css
                └── history/
                    ├── history.component.ts
                    ├── history.component.html
                    └── history.component.css
```
 
---
 
## ✨ Features
 
- 🔐 **User Authentication** — Register & login with in-memory storage
- 🌾 **Crop Price Prediction** — ML-powered price forecast per 20 kg
- 📊 **6-Month Trend Chart** — Visual price outlook after prediction
- 📋 **User-Specific History** — Each user sees only their own predictions
- ☁️ **Firebase Firestore** — Cloud storage for all prediction records
- 📱 **Responsive UI** — Works on desktop and mobile
 
---
 
## ✅ Prerequisites
 
Make sure the following are installed before setup:
 
| Tool | Version | Check |
|---|---|---|
| Node.js | 18+ | `node -v` |
| Angular CLI | 17+ | `ng version` |
| Java JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Python | 3.10+ | `python --version` |
| pip | latest | `pip --version` |
 
---
 
## ⚙️ Setup & Installation
 
### 1. Firebase Setup
 
1. Go to [Firebase Console](https://console.firebase.google.com/) and create a new project
2. Navigate to **Firestore Database** → click **Create database** → choose **Production mode**
3. Go to **Project Settings → Service Accounts**
4. Click **Generate new private key** → download the JSON file
5. Rename it to `firebase-service-account.json`
6. Place it at:
   ```
   backend/src/main/resources/firebase-service-account.json
   ```
 
> ⚠️ **Firestore Composite Index** — When you first call `/api/history`, Firestore will throw an error with a link in your console logs. Click that link to auto-create the required composite index (`userId` + `createdAt`). It takes ~1 minute to build.
 
---
 
### 2. AI Service (Python / Flask)
 
```bash
cd ai-service
 
# Install dependencies
pip install -r requirements.txt
 
# Train the ML model (creates models/ folder with .pkl files)
python train_model.py
 
# Start Flask server
python app.py
# → Running on http://localhost:5000
```
 
**Verify it's working:**
```bash
curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{"crop":"Wheat","market":"Surat","date":"2026-06-01"}'
```
 
**Expected response:**
```json
{
  "crop": "Wheat",
  "market": "Surat",
  "date": "2026-06-01",
  "predicted_price_per_20kg": 478.40,
  "predicted_price_per_quintal": 2392.00,
  "unit": "INR per 20 kg"
}
```
 
---
 
### 3. Backend (Spring Boot)
 
```bash
cd backend
 
# Run the application
mvn spring-boot:run
# → Running on http://localhost:8080
```
 
**Verify it's working:**
```bash
curl http://localhost:8080/api/health
# → {"status":"Backend running"}
```
 
---
 
### 4. Frontend (Angular)
 
```bash
cd frontend
 
# Install dependencies
npm install
 
# Start dev server
ng serve
# → Open http://localhost:4200
```
 
---
 
## 🚀 Running the Application
 
Open **3 separate terminals** and run in this order:
 
```bash
# Terminal 1 — AI Service (must be first)
cd ai-service
python app.py
 
# Terminal 2 — Backend
cd backend
mvn spring-boot:run
 
# Terminal 3 — Frontend
cd frontend
ng serve
```
 
Then open your browser at **http://localhost:4200**
 
**User flow:**
1. Visit `http://localhost:4200` → redirected to `/login`
2. Click **Register here** → create your account
3. Login with your credentials → redirected to `/predict`
4. Select crop, market, date → click **Predict Price**
5. View your personal history at `/history`
 
---
 
## 📡 API Reference
 
### Authentication
 
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login with email + password |
 
**Register request:**
```json
{
  "name": "Ravi Kumar",
  "email": "ravi@example.com",
  "password": "secret123"
}
```
 
**Register / Login response:**
```json
{
  "success": true,
  "message": "Login successful.",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Ravi Kumar",
  "email": "ravi@example.com"
}
```
 
---
 
### Prediction
 
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/predictPrice` | Get price prediction |
| GET | `/api/history?userId=` | Get user's prediction history |
| GET | `/api/health` | Health check |
 
**Predict request:**
```json
{
  "crop": "Wheat",
  "market": "Surat",
  "date": "2026-06-01",
  "userId": "550e8400-e29b-41d4-a716-446655440000"
}
```
 
**Predict response:**
```json
{
  "crop": "Wheat",
  "market": "Surat",
  "date": "2026-06-01",
  "predictedPricePer20kg": 478.40,
  "predictedPricePerQuintal": 2392.00,
  "unit": "INR per 20 kg",
  "timestamp": "2026-04-08T10:30:00Z"
}
```
 
**History response:**
```json
[
  {
    "id": "firestoreDocId",
    "crop": "Wheat",
    "market": "Surat",
    "date": "2026-06-01",
    "predictedPricePer20kg": 478.40,
    "predictedPricePerQuintal": 2392.00,
    "createdAt": "2026-04-08T10:30:00Z"
  }
]
```
 
---
 
## 🤖 ML Model Details
 
| Property | Value |
|---|---|
| Algorithm | RandomForestRegressor |
| Library | scikit-learn 1.5.0 |
| Data Processing | Pandas 2.2.2 |
| Numerical Ops | NumPy 1.26.4 |
| Train / Test Split | 80% / 20% |
| Target Variable | Price per quintal (INR) |
| Output Unit | Price per 20 kg (÷ 5) |
 
**Input features used for prediction:**
 
| Feature | Type | Description |
|---|---|---|
| crop | Encoded int | LabelEncoded crop name |
| market | Encoded int | LabelEncoded market name |
| month | int | Month of prediction date |
| year | int | Year of prediction date |
| day | int | Day of prediction date |
| quarter | int | Quarter (1–4) |
| week | int | ISO week number |
 
> To improve model accuracy, add more rows to `dataset/crop_prices.csv` and re-run `python train_model.py`.
 
---
 
## 🔥 Firebase Firestore Schema
 
**Collection:** `predictions`
 
```
predictions/
└── {auto-doc-id}/
    ├── id:                       "firestoreDocId"
    ├── userId:                   "550e8400-..."   ← links to user
    ├── crop:                     "Wheat"
    ├── market:                   "Surat"
    ├── date:                     "2026-06-01"
    ├── predictedPricePer20kg:    478.40
    ├── predictedPricePerQuintal: 2392.00
    └── createdAt:                "2026-04-08T10:30:00Z"
```
 
**Composite index required** (auto-created on first use):
 
| Field | Order |
|---|---|
| userId | Ascending |
| createdAt | Descending |
 
---
 
## 🔄 Data Flow
 
```
User fills form (crop + market + date)
    ↓
Angular reads userId from localStorage
    ↓
POST /api/predictPrice  { crop, market, date, userId }
    ↓
Spring Boot validates request
    ↓
POST http://localhost:5000/predict  { crop, market, date }
    ↓
Flask loads crop_price_model.pkl
    ↓
Pandas encodes features → NumPy array
    ↓
RandomForest predicts price per quintal
    ↓
Divide by 5 → price per 20 kg
    ↓
Return to Spring Boot
    ↓
Spring Boot saves to Firestore (with userId)
    ↓
Return PredictionResponse to Angular
    ↓
Angular displays result + 6-month trend chart
```
 
---
 
## 🚀 Future Enhancements
 
- Improve model accuracy with larger real-world datasets
- Add real-time market data integration (AGMARKNET API)
- Implement LSTM or XGBoost for better time-series prediction
- Add JWT-based authentication
- Firebase Authentication integration
- Push notifications for price alerts
- Multi-language support (Hindi, Gujarati)
- Mobile app (Android / iOS)
- Export history as PDF / CSV
 
---
 
## 📄 Note
 
This project is developed as a **Final Year Engineering Project** and demonstrates the integration of Machine Learning with full-stack web development using Angular, Spring Boot, Flask, and Firebase.
 
---
 
## 👨‍💻 Author : ASHVI DHANANI
 
**Computer Engineering Student**
