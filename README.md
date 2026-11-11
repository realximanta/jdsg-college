<div align="center">

  <h1>🎓 JDSG College Portal</h1>
  <p><b>The official digital ecosystem for Jogananda Deva Satradhikar Goswami (JDSG) College.</b></p>
  <p><i>Learn • Grow • Achieve</i></p>

  <a href="https://github.com/realximanta/jdsg-college/releases/latest">
    <img src="https://img.shields.io/github/v/release/realximanta/jdsg-college?style=for-the-badge&color=1E3A8A" alt="Latest Release" />
  </a>
  <a href="https://github.com/realximanta/jdsg-college/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/realximanta/jdsg-college/release.yml?style=for-the-badge&logo=githubactions&logoColor=white" alt="Build Status" />
  </a>
  <a href="https://api.jdsgcollege.dpdns.org/live">
    <img src="https://img.shields.io/uptimerobot/status/m797808888-8c3d7b9f3e4f5a6b7c8d9e0f?style=for-the-badge&label=API%20Status" alt="UptimeRobot Status" />
  </a>

  <br />

  <img src="https://jdsgcollege.edu.in/wp-content/uploads/2025/01/1-2.jpg" alt="JDSG College Campus" width="100%" style="border-radius: 12px; max-width: 800px;" />

</div>

---

## 📖 Table of Contents
- [Overview](#-overview)
- [✨ Key Features](#-key-features)
- [🏗️ System Architecture](#️-system-architecture)
- [💻 Tech Stack](#-tech-stack)
- [🚀 Getting Started](#-getting-started)
- [🔌 API Reference](#-api-reference)
- [🤝 Contributing](#-contributing)

---

## 🌟 Overview
This repository is a **Monorepo** housing the complete digital infrastructure for JDSG College. It replaces the need for manual noticeboard checking by providing real-time push updates, student/teacher profiles, and a seamless authentication flow.

Built for speed and scale, the architecture utilizes a **Polyglot Docker Container** deployed on Render's free tier, ensuring high availability at zero cost.

---

## ✨ Key Features

<table>
  <tr>
    <td width="33%" valign="top">
      <h3>📱 Android App</h3>
      <ul>
        <li>Native Jetpack Compose UI.</li>
        <li>Google Play Services Auth.</li>
        <li>Encrypted SharedPreferences.</li>
        <li>Dynamic Material 3 Dark Mode.</li>
        <li>Client-side WebP image compression.</li>
      </ul>
    </td>
    <td width="33%" valign="top">
      <h3>🌐 Web Portal</h3>
      <ul>
        <li>Zero-dependency Tailwind CSS.</li>
        <li>Google Identity Services.</li>
        <li>AOS (Animate On Scroll) micro-interactions.</li>
        <li>Responsive dashboard layout.</li>
        <li>Served directly via the Go Backend.</li>
      </ul>
    </td>
    <td width="33%" valign="top">
      <h3>⚙️ Backend</h3>
      <ul>
        <li>Go (net/http) API Gateway.</li>
        <li>Python (BeautifulSoup) auto-scraper.</li>
        <li>Cloudflare Workers KV (NoSQL).</li>
        <li>Cloudflare R2 Object Storage.</li>
        <li>JWT Session Management.</li>
      </ul>
    </td>
  </tr>
</table>

---

## 🏗️ System Architecture

```mermaid
graph TD
    User((User))
    
    subgraph Clients
        Android[📱 Android App\n(Kotlin/Compose)]
        Web[🌐 Web Portal\n(HTML/Tailwind)]
    end
    
    subgraph Render [☁️ Render Free Tier (Docker)]
        Go[🐹 Go API Gateway]
        Py[🐍 Python Scraper]
    end
    
    subgraph Cloudflare
        KV[(🗄️ Workers KV\nUser Data)]
        R2[(🖼️ R2 Bucket\nProfile Images)]
    end
    
    CollegeSite[🏫 College Website]
    Google[🔐 Google OAuth]
    
    User --> Android
    User --> Web
    
    Android --> Go
    Web --> Go
    
    Go --> Google
    Go --> KV
    Go --> R2
    
    Go -- os/exec --> Py
    Py -- Scrapes HTML --> CollegeSite
```

---

## 💻 Tech Stack

<div align="center">

  **Frontend**<br/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=flat-square&logo=tailwind-css&logoColor=white" />
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white" />

  **Backend & Storage**<br/>
  <img src="https://img.shields.io/badge/Go-00ADD8?style=flat-square&logo=go&logoColor=white" />
  <img src="https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=python&logoColor=white" />
  <img src="https://img.shields.io/badge/Cloudflare_R2-F38020?style=flat-square&logo=cloudflare&logoColor=white" />
  <img src="https://img.shields.io/badge/Cloudflare_KV-F38020?style=flat-square&logo=cloudflare&logoColor=white" />

  **DevOps**<br/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white" />
  <img src="https://img.shields.io/badge/Render-46E3B7?style=flat-square&logo=render&logoColor=white" />

</div>

---

## 🚀 Getting Started

### Prerequisites
- Go 1.26+
- Python 3.x
- Android Studio (for the app)
- Cloudflare Account (R2 & KV)
- Google Cloud Project (OAuth)

### 1. Backend Setup
```bash
cd backend
cp .env.example .env
# Edit .env with your Cloudflare and Google Client ID secrets
go mod tidy
go run main.go
```

### 2. Android Setup
1. Open the `app/` folder in Android Studio.
2. Create a `local.properties` file in the `app/` directory:
   ```properties
   sdk.dir=/path/to/android/sdk
   WEB_CLIENT_ID=your_google_web_client_id.apps.googleusercontent.com
   ```
3. Sync and Build.

### 3. Deployment (Render)
Render automatically deploys when changes are pushed to the `main` branch. Ensure the following Environment Variables are set in the Render Dashboard:
<details>
<summary>View Required Environment Variables</summary>

- `GOOGLE_CLIENT_ID`
- `JWT_SECRET`
- `CF_ACCOUNT_ID`
- `CF_API_TOKEN`
- `CF_KV_NAMESPACE_ID`
- `R2_ACCESS_KEY_ID`
- `R2_SECRET_ACCESS_KEY`
- `R2_BUCKET_NAME`
</details>

---

## 🔌 API Reference

| Endpoint | Method | Auth Required | Description |
| :--- | :--- | :---: | :--- |
| `/api/auth/google` | `POST` | ❌ | Validates Google ID Token, returns JWT & User data. |
| `/api/users/me` | `GET` | ✅ | Fetches the logged-in user's profile from KV. |
| `/api/users/me` | `PUT` | ✅ | Updates/Creates profile metadata. |
| `/api/users/me/avatar` | `POST` | ✅ | Uploads a WebP image to Cloudflare R2. |
| `/api/notifications` | `GET` | ❌ | Fetches and caches the latest college notices via Python. |
| `/live` | `GET`/`HEAD` | ❌ | Health check for UptimeRobot keep-alive. |

---

## 🤝 Contributing
This project is currently maintained as a closed-source internal tool for JDSG College, but architectural reviews and bug reports are welcome via GitHub Issues.

---
<div align="center">
  <p>Built with ❤️ for JDSG College, Bokakhat.</p>
  <p><i>Architected by <a href="https://github.com/realximanta">realximanta</a></i></p>
</div>
