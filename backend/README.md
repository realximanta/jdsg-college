<h1 align="center">
  <a href="https://git.io/typing-svg">
    <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=32&pause=1000&color=3B82F6&center=true&vCenter=true&width=600&lines=JDSG+College+Backend;Go+%2B+Python+%2B+Docker;Secure+%26+Fast+API+Gateway" alt="Typing SVG" />
  </a>
</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Go-1.22+-00ADD8?style=for-the-badge&logo=go&logoColor=white" alt="Go Version" />
  <img src="https://img.shields.io/badge/Python-3.xx-3776AB?style=for-the-badge&logo=python&logoColor=white" alt="Python" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Render-Deploying-46E3B7?style=for-the-badge&logo=render&logoColor=white" alt="Render" />
  <img src="https://img.shields.io/badge/Cloudflare-R2_&_KV-F38020?style=for-the-badge&logo=cloudflare&logoColor=white" alt="Cloudflare" />
</p>

---

## 🚀 What Does This Backend Do?
This backend acts as the central nervous system for the JDSG College App. It bridges the gap between the Android client, the college's official website, and our cloud storage.

*   🔐 **Authentication:** Securely verifies Google OAuth tokens and issues 7-day JWT sessions.
*   🗄️ **Database (KV):** Stores user profiles (students and teachers) in Cloudflare Workers KV for ultra-fast reads.
*   🖼️ **Image Storage (R2):** Handles compressed profile picture uploads directly to Cloudflare R2.
*   🕷️ **Web Scraping:** Uses a Python script triggered via Go (`os/exec`) to scrape college notifications.
*   🐳 **Polyglot Environment:** Runs on a single Docker container that includes Go (API), Python (Scraper), Node.js (future chat), and PHP (future web portal).

---

## 📂 File Structure

```text
backend/
│
├── internal/                 # Private application code (not imported by other projects)
│   ├── auth/                 # Authentication Logic
│   │   ├── google.go         # Verifies Google ID tokens via the official Google API
│   │   └── jwt.go            # Generates and verifies 7-day session JWTs
│   │
│   ├── config/               # Configuration Management
│   │   └── config.go         # Loads .env variables safely into a struct
│   │
│   ├── handlers/             # HTTP Route Controllers
│   │   ├── auth.go           # Handles /api/auth/google (Login/Verify)
│   │   ├── notifications.go  # Handles /api/notifications (Executes Python scraper)
│   │   └── users.go          # Handles /api/users (Signup, Profile, Avatar Upload)
│   │
│   ├── middleware/           # HTTP Middleware
│   │   └── cors.go           # Injects CORS headers for web/app frontend
│   │
│   ├── models/               # Data Structures
│   │   └── user.go           # Defines User struct, JSON tags, and Auth structs
│   │
│   └── storage/              # Cloud Storage Clients
│       ├── kv.go             # REST API client for Cloudflare Workers KV
│       └── r2.go             # AWS SDK v2 client for Cloudflare R2 (S3-compatible)
│
├── scripts/                  # Scripts executed by the Go backend
│   └── scraper.py            # Python BeautifulSoup scraper for JDSG notifications
│
├── .dockerignore             # Excludes git/docs from Docker build context
├── .env.example              # Template for required environment variables
├── Dockerfile                # Multi-stage build (Go + Python + Node + PHP)
├── go.mod                    # Go module dependencies and version
├── main.go                   # Main entry point (starts server, mounts routes)
└── README.md                 # Project documentation
```

---

## 🔌 API Endpoints

| Method | Endpoint | Role | Requires Auth? |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/google` | Verifies Google ID token. Returns JWT if user exists, or signals the app to show the signup form. | ❌ |
| `POST` | `/api/users` | Completes the signup process. Saves user role, class/dept, phone, and profile URL to KV. | ✅ |
| `GET` | `/api/users/me` | Fetches the authenticated user's profile from KV. | ✅ |
| `PUT` | `/api/users/me` | Updates the user's profile details (phone, class, etc.). | ✅ |
| `POST` | `/api/users/me/avatar` | Accepts multipart image, uploads to Cloudflare R2, updates KV with the new public URL. | ✅ |
| `GET` | `/api/notifications` | Triggers the Python scraper, caches the results for 10 mins, and returns JSON. | ❌ |
| `GET/HEAD`| `/live` | Health check endpoint for UptimeRobot to keep the Render instance awake. | ❌ |

---

## 🛠️ How to Deploy on Render

1. Push this repository to GitHub.
2. Go to [Render Dashboard](https://dashboard.render.com) and create a **New Web Service**.
3. Connect your GitHub repo and select the `backend` folder as the root directory (if using a monorepo).
4. Render will automatically detect the `Dockerfile` and choose the **Docker** environment.
5. Select the **Free Tier**.
6. Add the following **Environment Variables** in the Render dashboard:
   - `PORT` (Render sets this automatically, but defaults to 8080)
   - `GOOGLE_CLIENT_ID`
   - `JWT_SECRET` (e.g., output of `openssl rand -hex 32`)
   - `CF_ACCOUNT_ID`
   - `R2_ACCESS_KEY_ID`
   - `R2_SECRET_ACCESS_KEY`
   - `R2_BUCKET_NAME`
   - `CF_API_TOKEN`
   - `CF_KV_NAMESPACE_ID`
7. Set the **Health Check Path** to `/live`.
8. Click **Create Web Service**. Render will build the Docker image and deploy.

---

## 💻 How to Run Locally

If you want to run this on your own machine:

1. **Install Prerequisites:** Go 1.22+ and Python 3.x.
2. **Install Go Dependencies:**
   ```bash
   go mod tidy
   ```
3. **Install Python Dependencies:**
   ```bash
   pip install requests beautifulsoup4
   ```
4. **Setup Environment Variables:** Copy `.env.example` to `.env` and fill in your secrets.
5. **Run the Server:**
   ```bash
   go run main.go
   ```
   *The server will start on `http://localhost:8080`.*

<p align="center">
  <i>Built for JDSG College, Bokakhat.</i>
</p>
