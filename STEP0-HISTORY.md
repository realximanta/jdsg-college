# STEP 0: Project History & Blueprint - 
**UPDATE** *04-05/11/2026*

This document serves as the absolute source of truth for the JDSG College App and Web project. It records every architectural decision, feature flow, and requirement discussed before the first line of code was written. 

**Security Note:** Absolutely no credentials, SHA-1 hashes, passwords, or API keys are stored in this document or this repository. All secrets live in GitHub Actions Secrets and Render Environment Variables.

---

## 📌 1. Core Identity
- **App Name:** JDSG College (Display Name)
- **Application ID:** `in.edu.jdsgcollege.bokakhat`
- **Short Domain:** `jdsgcollege.dpdns.org`
- **Long Domain:** `joganandadevasatradhikargoswamicollegebokakhat.dpdns.org`

## 📌 2. Tech Stack
- **Android App:** Kotlin, Jetpack Compose (MVVM), Retrofit (API), Coil (Images), EncryptedSharedPreferences (JWT storage).
- **Backend:** Go (Golang), Dockerized, Hosted on Render (Free Tier).
- **Storage:** Cloudflare R2 (Profile images, compressed WebP) + Cloudflare Workers KV (User metadata).
- **CI/CD:** GitHub Actions (Auto-bump version, build signed APK, publish release).
- **Keep-Alive:** UptimeRobot pinging `/live` endpoint every 5 minutes.

## 📌 3. App Flow & Screen Details

### A. Landing Screen (Unauthenticated)
- **Content:** About the Home of College (similar feel to the official jdsgcollege.edu.in).
- **Top Right Corner:** "Sign Up" and "Login" buttons.
- **Hamburger Menu (Top Left):**
  - *About:* In-app screen with a back button, sharing the same content as the Landing Screen.
  - *Privacy:* Opens external browser URL.
  - *Terms:* Opens external browser URL.

### B. Authentication (Sign Up / Login)
- **Google Sign-In:** Must use native Android `Credential Manager` / Play Services Auth (NO WebViews). Scope restricted strictly to `userinfo.email` and `userinfo.profile`.
- **Login Screen:** "Welcome Back" text, "Continue with Google" button.
- **Sign Up Screen:** "Continue with Google" first, then reveals actual fields based on chosen role.

#### Sign-Up Fields (Strictly Enforced)
- **Role Selection:** Student or Teacher.
- **If Student:**
  - Full Name* (Required)
  - Class* (Required): HS 1st Year Arts, HS 1st Year Commerce, HS 2nd Year Arts, HS 2nd Year Commerce, B.A., B.COM.
  - Semester* (Required ONLY if B.A. or B.COM is selected): I to VIII.
  - Roll No (Optional)
  - Phone No* (Required, exactly 10 digits, Indian format 6-9 start).
  - Profile Picture* (Required).
  - Emergency Contact (Optional).
- **If Teacher:**
  - Full Name* (Required)
  - Department* (Required): Assamese, Commerce, Economics, Education, English, History, Philosophy, Political Science, Geography (New).
  - Designation* (Required): Asst. Professor / HOD.
  - Phone No* (Required, exactly 10 digits).
  - Profile Picture* (Required).

### C. Authenticated Dashboard
- **Bottom Navigation Bar:** Home | Notifications | My Account

### D. Screens
- **Home:** Placeholder for now (routine/room locators to be added later).
- **Notifications:** Pulls data via backend from `https://jdsgcollege.edu.in/category/notifications/`. 
- **My Account:** Instagram-style profile screen. Displays Full Name, Role, Department/Class, Phone, Profile Picture. Includes an "Update Profile" button (all fields mandatory, must not be empty).

## 📌 4. Backend Architecture (Go)
- **API Endpoints:**
  - `POST /api/auth/google` (Verify token, issue JWT)
  - `GET /api/users/me` (Get profile)
  - `POST /api/users` (Complete signup)
  - `PUT /api/users/me` (Update profile)
  - `POST /api/users/me/avatar` (Handle image upload)
  - `GET /api/notifications` (Proxy/scrape college site)
  - `GET /live` & `HEAD /live` (UptimeRobot health check)
- **Notifications Scraper:** Go backend will parse the WP REST API or scrape the HTML of the college notifications page, cache it for 5-10 mins, and serve clean JSON to the app/web.
- **Storage Flow:** App compresses image to max 2048x2048 WebP (~500KB or less), sends to Go backend, Go uploads to Cloudflare R2, saves the public CDN URL to Workers KV.

## 📌 5. Design Language
- **Vibe:** Minimal, official, modern. Institutional blue/teal accents.
- **Typography:** Inter or Satoshi for UI, Serif for official college name.
- **Layout:** Card-based notification feed, clean spacing, no clutter.
- **Icons:** Official college logo (provided in assets).

## 📌 6. Open Decisions / Side Quests
- Profile picture *is* mandatory at signup (as requested), but we must ensure aggressive client-side compression so users don't bounce due to slow uploads.
- The "password-protected URL" idea for R2 was scrapped in favor of an authenticated Go API endpoint (`GET /api/user/:id`).
