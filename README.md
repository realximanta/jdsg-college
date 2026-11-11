<h1 align="center">
  <a href="https://git.io/typing-svg">
    <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=700&size=32&pause=1000&color=1E3A8A&center=true&vCenter=true&width=600&lines=JDSG+College+Portal;Android+App+%2B+Web+App;Learn+%E2%80%A2+Grow+%E2%80%A2+Achieve" alt="Typing SVG" />
  </a>
</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-2024.06-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Go-1.26+-00ADD8?style=for-the-badge&logo=go&logoColor=white" alt="Go" />
  <img src="https://img.shields.io/badge/Python-3.x-3776AB?style=for-the-badge&logo=python&logoColor=white" alt="Python" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Cloudflare-R2_&_KV-F38020?style=for-the-badge&logo=cloudflare&logoColor=white" alt="Cloudflare" />
</p>

---

## 🏛️ About the Project
The official digital portal for **Jogananda Deva Satradhikar Goswami (JDSG) College**, Bokakhat. This monorepo contains both the **Android Application** and the **Polyglot Backend** (which also serves a responsive Web Portal). 

The project aims to provide students and faculty with a seamless, secure, and modern interface to interact with the institution, check notifications, and manage profiles.

---

## ✨ Features
- 🔐 **Google Authentication:** Secure login/signup via native Google Play Services (Android) and Google Identity Services (Web).
- 📰 **Campus Notifications:** Automated Python scraper fetches the latest news from the college website.
- 👤 **Profile Management:** Instagram-style profile view and update flow with image compression.
- 🎨 **Premium UI/UX:** Material 3 dynamic theming with a custom "Deep Slate" dark mode and Serif typography.
- 🌐 **Web Portal:** Fully responsive web version served directly by the Go backend.
- 🐳 **Polyglot Backend:** Single Docker container running Go (API), Python (Scraper), Node.js (Future Chat), and PHP (Future Portal).

---

## 🛠️ Tech Stack
- **Android App:** Kotlin, Jetpack Compose (Material 3), Retrofit, Coil, EncryptedSharedPreferences.
- **Backend:** Go (net/http), Python (BeautifulSoup).
- **Database & Storage:** Cloudflare Workers KV (Metadata), Cloudflare R2 (Profile Images).
- **Frontend (Web):** HTML5, Tailwind CSS (CDN), AOS (Animate on Scroll).
- **CI/CD:** GitHub Actions.
- **Hosting:** Render (Backend & Web), Cloudflare (Storage & DNS).

---

## 📂 Repository Structure
```text
jdsg-college/
│
├── app/                  # Android Application (Kotlin + Compose)
│   ├── src/main/java/edu/jdsgcollege/bokakhat/
│   │   ├── data/         # Retrofit, Encrypted SharedPreferences
│   │   ├── ui/           # Screens, Theme, ViewModels
│   │   ├── navigation/   # App NavHost
│   │   └── utils/        # Image Compression
│   └── build.gradle.kts  # App-level Gradle config
│
├── backend/              # Polyglot Backend (Go + Python + Docker)
│   ├── internal/         # Go Handlers, Models, Auth, Storage
│   ├── scripts/          # Python Scraper (scraper.py)
│   ├── public/           # Web Portal (HTML/CSS/JS)
│   ├── Dockerfile        # Multi-stage polyglot container
│   └── main.go           # Server entry point
│
├── HISTORY/              # Architectural documentation (STEP0, STEP1, etc.)
│
└── .github/workflows/    # CI/CD pipelines (release.yml, generate-keystore.yml)
```

---

## 🔌 API Endpoints (Go Backend)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/google` | Verifies Google ID token. Returns JWT + User data (or prompts signup). |
| `PUT` | `/api/users/me` | Creates/updates the user profile in KV. |
| `GET` | `/api/users/me` | Fetches the authenticated user's profile. |
| `POST` | `/api/users/me/avatar` | Uploads compressed image to Cloudflare R2. |
| `GET` | `/api/notifications` | Triggers the Python scraper (cached for 10 mins). |
| `GET` | `/live` | Health check endpoint for UptimeRobot. |

---

## 🚀 Local Development

### 1. Backend (Go + Docker)
```bash
cd backend
cp .env.example .env # Fill in your Cloudflare and Google secrets
go mod tidy
go run main.go
```
*Note: To test the scraper locally, ensure `python3` and `requests`, `beautifulsoup4` are installed.*

### 2. Android App
1. Open the `app/` folder in **Android Studio**.
2. Create a `local.properties` file in the `app/` directory:
   ```properties
   sdk.dir=YOUR_SDK_PATH
   WEB_CLIENT_ID=your_actual_web_client_id.apps.googleusercontent.com
   ```
3. Sync Gradle and run on an emulator/device.

---

## 🚀 Release Process (CI/CD)
This repository uses GitHub Actions to automatically build and sign the APK.
1. Create a new release tag (e.g., `v2.0.3`).
2. The `release.yml` workflow triggers automatically.
3. It bumps the `versionCode` and `versionName` in `build.gradle.kts`.
4. It builds the signed APK and attaches it to the GitHub Release.

---
<p align="center">
  <i>Built for JDSG College, Bokakhat. Developed by <a href="https://github.com/realximanta">realximanta</a>.</i>
</p>
