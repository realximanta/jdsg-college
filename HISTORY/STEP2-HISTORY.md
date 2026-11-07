# STEP 2: Android Client & CI/CD Implementation
**UPDATE** *07/11/2026*

This document records the implementation of the Android application using Kotlin and Jetpack Compose, the critical backend fix required for signup, and the finalized CI/CD pipeline.

**Security Note:** No credentials, API keys, or passwords are stored here. All secrets are managed via GitHub Secrets and local `local.properties`.

---

## 📌 1. Android App Architecture
The Android client is built using a modern, robust stack:
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Networking:** Retrofit + OkHttp + Gson
- **Image Loading:** Coil (for R2 profile pictures)
- **Security:** AndroidX Security Crypto (EncryptedSharedPreferences) for secure JWT session storage.

## 📌 2. Critical Backend Fix: Signup Token
A logical flaw was discovered during Android implementation: the backend required a JWT to call `POST /api/users` (signup), but the Google Auth response for new users did not provide one.
- **The Fix:** Updated `backend/internal/handlers/auth.go`. When a user doesn't exist (`userData == nil`), the backend now generates a temporary JWT (`signupToken`) and returns it alongside the `exists: false` payload. This allows the Android app to authenticate the subsequent signup and avatar upload calls.

## 📌 3. UI/UX Implementation Details
- **Landing Screen:** Implemented as a `HorizontalPager` (3 slides: College Front, Principal's Message, College Details). Includes overlay hamburger menu and top-right Sign Up/Login buttons.
- **Auth Screen:** Integrates native Google Sign-In intent. Dynamically switches to the signup form if the backend indicates the user is new. Enforces 10-digit phone numbers and mandatory profile pictures.
- **Dashboard:** Bottom Navigation Bar (Home, Notifications, My Account) using `NavHost`.
- **Account Screen:** Instagram-style layout. Displays dynamic user data (Role, Class/Dept, Phone). Includes a button to navigate to the Update Profile screen.
- **Notifications Screen:** `LazyColumn` fetching data from the Go backend, handling Loading, Error, and Empty states. Clicking a card opens the URL in the device's browser.
- **Update Profile:** Pre-fills current user data. Enforces mandatory fields. Chained API calls: Uploads new avatar to R2 first (if changed), then saves the updated text fields to KV.

## 📌 4. Image Compression Logic
To respect Cloudflare R2 free-tier limits and ensure fast uploads, the app compresses images locally before sending them.
- **Utility:** `ImageUtils.kt`
- **Logic:** Resizes to a maximum of 2048x2048 pixels, compresses to WebP format at 80% quality, and saves to the app's cache directory.
- **Safety Net:** The Go backend enforces a hard 2MB limit via `http.MaxBytesReader` to prevent abuse.

## 📌 5. CI/CD Pipeline (Monorepo)
The GitHub Actions workflow (`.github/workflows/release.yml`) was updated to handle the monorepo structure.
- **Version Bump:** Reads and updates `app/app/build.gradle.kts`.
- **Local Properties:** Dynamically generates `app/local.properties` during the build to inject the `WEB_CLIENT_ID` from GitHub Secrets.
- **Build:** Compiles the APK using the `app/` directory.
- **Release:** Attaches the generated `jdsg-college-vX.Y.Z.apk` to the GitHub Release.

## 📌 6. File Structure Summary
- `app/app/src/main/java/.../data/remote/`: API interface, Retrofit client, Data classes.
- `app/app/src/main/java/.../data/local/`: Encrypted SharedPreferences.
- `app/app/src/main/java/.../ui/screens/`: All Compose UI screens (Landing, Auth, Home, Notifications, Account, UpdateProfile).
- `app/app/src/main/java/.../ui/viewmodels/`: State management.
- `app/app/src/main/java/.../utils/`: Image compression utilities.
