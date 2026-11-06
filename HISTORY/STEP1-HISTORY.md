# STEP 1: Backend Architecture & Polyglot Integration
**UPDATE** *06/11/2026*

This document records architectural updates and decisions made after the creation of `STEP0-HISTORY.md`. It captures the shift to a polyglot backend, the finalized Go API structure, and every granular detail of the backend codebase implemented in this step.

**Security Note:** No credentials, API keys, or passwords are stored here. All secrets are managed via Render Environment Variables and GitHub Secrets.

---

## 📌 1. Polyglot Backend Strategy (The Shift)
We have adopted a single-container polyglot architecture to handle specific tasks efficiently while staying within Render's 512MB RAM free-tier limit.
- 🐹 **Go (Main API Gateway):** Handles all routing, JWT auth, Google OAuth verification, and Cloudflare API communication. Chosen for its speed, static compilation, and low memory footprint.
- 🐍 **Python (Data Scraper):** Used specifically to scrape the JDSG College website. `BeautifulSoup` handles messy WordPress HTML much better than pure Go scraping libraries. Called silently via `os/exec`.
- 🟢 **Node.js (Future Chat):** Will handle real-time WebSockets for the student-teacher messaging system (planned to auto-delete messages after 28 hours).
- 🐘 **PHP (Future Web Portal):** Will render the server-side web version of the app.

## 📌 2. Backend File-by-File Contribution Breakdown

### Root & Configuration
- `go.mod`: Defines the Go module path (`github.com/realximanta/jdsg-college/backend`) and Go version.
- `.env.example`: Template detailing all required secrets (Google, JWT, Cloudflare R2 & KV).
- `.dockerignore`: Excludes git history and markdown files from the Docker build context to keep image builds fast.
- `Dockerfile`: Multi-stage build that compiles Go, then creates an Alpine runtime containing Go, Python3, Node.js, and PHP81 to satisfy the polyglot requirement.
- `render.yaml`: Infrastructure-as-code for Render. Points to the Dockerfile, sets the free tier, and configures the `/live` health check.
- `README.md`: Comprehensive documentation with animated shields, file structure, API endpoints, and deployment steps.
- `main.go`: The central bootstrap. Loads config, initializes KV/R2 clients, mounts all API handlers, wraps them in CORS middleware, and starts the HTTP server.

### Internal Code (`internal/`)
- `config/config.go`: Loads environment variables into a typed struct for safe access across the app.
- `models/user.go`: Defines the `User` struct handling both Student (Class/Semester/Roll) and Teacher (Department/Designation) fields, plus Auth request/response structs.
- `auth/google.go`: Uses `google.golang.org/api/idtoken` to cryptographically verify the Android client's Google ID token and extracts the email/name.
- `auth/jwt.go`: Generates and validates 7-day session tokens using `golang-jwt/jwt/v5`.
- `storage/kv.go`: A custom HTTP client that interacts with Cloudflare Workers KV via REST API to `Put` and `Get` user JSON data.
- `storage/r2.go`: Uses AWS SDK v2 (S3-compatible) configured with a custom endpoint to upload WebP profile images to Cloudflare R2 and return the public CDN URL.
- `middleware/cors.go`: Injects CORS headers, allowing the future web portal and Android app to communicate with the Go backend securely.
- `handlers/auth.go`: Receives Google ID token, verifies it, checks KV for existing user, and either issues a JWT or tells the app to proceed to Signup.
- `handlers/users.go`: Manages the complete user lifecycle. Enforces 10-digit phone numbers, validates the R2 image upload flow, verifies JWTs via the Authorization header, and updates KV metadata.
- `handlers/notifications.go`: Replaces pure Go scraping with `os/exec`. Runs the Python script, parses the returned JSON, caches it in memory for 10 minutes to prevent college site DDoS, and provides a stale-cache fallback.

### Scripts
- `scripts/scraper.py`: A standalone Python 3 script using `requests` and `BeautifulSoup`. Fetches `jdsgcollege.edu.in/category/notifications/`, parses the WordPress article structure, and prints a clean JSON array to stdout.

## 📌 3. API Endpoints & Data Flow
- `POST /api/auth/google`: `{ "id_token": "..." }` → Returns `{ "exists": true/false, "token": "JWT", "user": {...} }`.
- `POST /api/users`: (Requires Bearer JWT) Finalizes signup. Enforces mandatory fields (Phone length, Role constraints).
- `GET /api/users/me`: (Requires Bearer JWT) Fetches the user profile directly from Cloudflare KV.
- `PUT /api/users/me`: (Requires Bearer JWT) Updates profile fields and saves back to KV.
- `POST /api/users/me/avatar`: (Requires Bearer JWT) Accepts multipart form-data (`avatar`), uploads to R2, updates KV with the new URL.
- `GET /api/notifications`: Triggers the `os/exec` call to Python, caches the result in memory, and returns JSON.
- `GET`/`HEAD` `/live`: Returns 200 OK to keep Render awake via UptimeRobot.

## 📌 4. Key Technical Decisions & Constraints
- **Notifications Cache:** To avoid hammering the college website or timing out, the Go backend caches Python's scraper output in memory for 10 minutes.
- **Image Constraints:** The Android client is responsible for compressing images to WebP (<500KB). The Go backend enforces a hard 2MB limit via `http.MaxBytesReader` as a safety net.
- **Session Management:** JWTs are issued for 7 days. The Android app will store this securely in EncryptedSharedPreferences.
- **Memory Management:** Since Node.js and PHP are installed in the Docker image but not running yet, they consume 0MB of RAM until we implement the chat and web portal later.
