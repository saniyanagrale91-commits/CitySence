Project Name: Citysence – Smart Civic & Healthcare Management
Table of Contents

Project Overview

Features

Modules & Workflows

Machine Learning Integration

Firebase Database Structure

Screenshots / UI

Installation

Usage

Future Scope

References

Project Overview

Citysence is a mobile application designed for smart city management, focusing on:

Environment Detection: Detect civic issues like garbage, water wastage, electricity faults using ML and GPS.

Healthcare Appointment: Prioritize patients using ML-based severity scoring.

Admin Dashboard: Monitor complaints and appointments efficiently.

Tech Stack:

Frontend: Android (Java + XML)

Backend: Firebase Realtime Database & Authentication

Machine Learning: TensorFlow / TFLite models for image classification & symptom severity scoring

Features

Google Sign-In Authentication (Admin/User)

Image-based environment issue detection

Symptom-based health appointment booking with ML priority score

Real-time complaint & appointment tracking for Admin

Location tracking & timestamping for complaints

Modules & Workflows
1️⃣ Splash Screen → Login → Home Page

Splash screen loads for 2–3 seconds → Checks if user logged in → Redirects accordingly

2️⃣ Environment Detection & Complaint Module

Capture image → ML model detects issues → Add description → Auto-location → Submit complaint → Stored in Firebase

3️⃣ Book Appointment – Healthcare Module

Select symptoms → ML model computes severity → Appointment stored → Queue management & status view

4️⃣ Admin Module

Predefined Google Admin login

View complaints with images, description, location

Monitor appointments sorted by priority

Option to call patients and schedule appointments

Machine Learning Integration

Environment Module: Image Classification / Object Detection → Detects issues like garbage, water wastage, electricity faults

Healthcare Module: Symptom Classification → Generates priority score for appointment scheduling

Firebase Database Structure
Firebase Database
│
├── USERS
│   └── user_uid_123
│       ├── name
│       ├── phone
│ │       └── home_address
│
├── COMPLAINTS
│   └── complaint_001
│       ├── description
│       ├── detected_issues
│       ├── location
│       ├── timestamp
│       └── user_id
│
├── APPOINTMENTS
│   └── appointment_A01
│       ├── name
│       ├── phone
│       ├── home_address
│       ├── symptoms
│       ├── priority_score
│       ├── timestamp
│       └── user_id
Screenshots / UI

(Add images of Splash Screen, Login, Home Page, Complaint Module, Appointment Module, Admin Dashboard)

Installation

Clone the repository

git clone https://github.com/your-username/citysense.git

Open project in Android Studio

Add google-services.json in app/ folder

Build & Run on emulator or physical device

Usage

Open the app → Login with Google

Choose a module (Environment / Healthcare)

Submit complaints or book appointments

Admin can monitor all records via Admin Dashboard

Future Scope

Integrate traffic, water, emergency SOS modules

Use advanced ML models for real-time issue detection

Push notifications to users and authorities

References

Firebase Documentation: https://firebase.google.com/docs

TensorFlow Keras Documentation: https://www.tensorflow.org/guide/keras

Android Developers Guide: https://developer.android.com/docs
