<h1 align="center">
  <a href="https://git.io/typing-svg">
    <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=32&pause=1000&color=3DDC84&center=true&vCenter=true&width=600&lines=JDSG+College+Android+App;Kotlin+%2B+Jetpack+Compose;Secure+%26+Fast+Client" alt="Typing SVG" />
  </a>
</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-2024.06-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Android-SDK_34-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Google_OAuth-Enabled-DB4437?style=for-the-badge&logo=google&logoColor=white" alt="Google Auth" />
</p>

---

## 🚀 What Does This App Do?
This is the official Android client for the JDSG College App. It provides students and teachers with a seamless, secure, and modern interface to interact with the institution.

*   🔐 **Google Authentication:** Secure login and signup using native Google Play Services.
*   📰 **Campus Notifications:** Fetches the latest news and notices directly from the college website via our Go backend.
*   👤 **Student/Teacher Profiles:** Instagram-style profile management with Cloudflare R2 image hosting.
*   📱 **Modern UI/UX:** Built entirely with Jetpack Compose and Material 3.

---

## 📂 File Structure

```text
app/
│
├── app/                          # Main Android Module
│   ├── src/main/
│   │   ├── AndroidManifest.xml   # App declaration (Internet permissions, MainActivity)
│   │   ├── java/in/edu/jdsgcollege/bokakhat/
│   │   │   ├── MainActivity.kt   # Entry point, sets up Theme and Navigation
│   │   │   ├── data/             # Data Layer
│   │   │   │   ├── local/        # Encrypted SharedPreferences (JWT storage)
│   │   │   │   └── remote/       # Retrofit clients, API interfaces, DTO models
│   │   │   ├── navigation/       # Top-level NavHost (Landing -> Auth -> Dashboard)
│   │   │   ├── ui/               # UI Layer
│   │   │   │   ├── screens/      # Compose UI (Landing, Auth, Profile, Notifications, etc.)
│   │   │   │   ├── theme/        # Colors, Typography, Shapes
│   │   │   │   └── viewmodels/   # State management (AuthViewModel, NotificationViewModel)
│   │   │   └── utils/            # Image compression, helpers
│   │   └── res/                  # Strings, XML themes, backup rules
│   ├── build.gradle.kts          # Module-level build config (Signing, Dependencies)
│   └── proguard-rules.pro        # Release build obfuscation rules
│
├── gradle/                       # Gradle Wrapper
├── build.gradle.kts              # Project-level build config
├── gradle.properties             # Global Gradle properties
├── local.properties              # (Ignored) Local SDK path & Web Client ID
└── settings.gradle.kts           # Module inclusion
```

---

## 🛠️ How to Build Locally

1.  Clone the repository and open the `app` folder in **Android Studio**.
2.  Ensure you have **JDK 17** installed.
3.  Create a `local.properties` file in the `app/` directory and add your SDK path and Google Web Client ID:
    ```properties
    sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
    WEB_CLIENT_ID=your_actual_web_client_id.apps.googleusercontent.com
    ```
4.  Sync Gradle and click **Run**.

---

## 🚀 How to Release (CI/CD)

This app is equipped with a GitHub Actions pipeline (`.github/workflows/release.yml`).

1.  Go to the GitHub repository.
2.  Click **Releases** -> **Draft a new release**.
3.  Create a tag (e.g., `v1.0.0`) and click **Publish release**.
4.  The workflow will automatically bump the version, build a signed APK using your GitHub Secrets, and attach it to the release.

<p align="center">
  <i>Built for JDSG College, Bokakhat.</i>
</p>
