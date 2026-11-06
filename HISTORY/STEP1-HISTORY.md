# STEP 1: Backend Architecture & Polyglot Integration
**UPDATE** *06/11/2026*

This document records architectural updates and decisions made after the creation of `STEP0-HISTORY.md`. It captures the shift to a polyglot backend, the finalized Go API structure, and the refined UI/UX details for the Landing Screen.

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
- `go.mod`: Defines the Go module path and version.
- `.env.example`: Template detailing all required secrets.
- `.dockerignore`: Excludes git history and markdown files from the Docker build context.
- `Dockerfile`: Multi-stage build compiling Go, then setting up Alpine with Python3, Node.js, and PHP81.
- `render.yaml`: Infrastructure-as-code for Render.
- `README.md`: Comprehensive documentation with animated shields, file structure, and deployment steps.
- `main.go`: The central bootstrap. Loads config, initializes KV/R2 clients, mounts API handlers, and starts the HTTP server.

### Internal Code (`internal/`)
- `config/config.go`: Loads environment variables into a typed struct.
- `models/user.go`: Defines the `User` struct handling Student and Teacher fields.
- `auth/google.go`: Verifies the Android client's Google ID token.
- `auth/jwt.go`: Generates and validates 7-day session tokens.
- `storage/kv.go`: Interacts with Cloudflare Workers KV to `Put` and `Get` user JSON.
- `storage/r2.go`: Uploads WebP profile images to Cloudflare R2.
- `middleware/cors.go`: Injects CORS headers for frontend communication.
- `handlers/auth.go`: Verifies Google token, checks KV, issues JWT or prompts signup.
- `handlers/users.go`: Enforces 10-digit phone validation, handles image uploads, updates KV metadata.
- `handlers/notifications.go`: Executes the Python script, caches JSON for 10 minutes.

### Scripts
- `scripts/scraper.py`: Fetches `jdsgcollege.edu.in/category/notifications/` and prints a clean JSON array.

## 📌 3. API Endpoints & Data Flow
- `POST /api/auth/google`: Returns `{ "exists": true/false, "token": "JWT", "user": {...} }`.
- `POST /api/users`: Finalizes signup (Enforces mandatory fields).
- `GET /api/users/me`: Fetches user profile from KV.
- `PUT /api/users/me`: Updates profile fields in KV.
- `POST /api/users/me/avatar`: Accepts multipart form-data, uploads to R2, updates KV.
- `GET /api/notifications`: Triggers Python scraper, caches result, returns JSON.
- `GET`/`HEAD` `/live`: Health check for UptimeRobot.

## 📌 4. UI/UX Refinement: Landing Screen Carousel
The Landing Screen (Unauthenticated) is not a static page. It is a swipeable 3-slide carousel (ViewPager) designed to feel official and informative.
- **Slide 1:** Features the college front picture (to be provided by Ximanta). Below the image: "JDSG College" in Serif font, and the subtitle "Learn • Grow • Achieve".
- **Slide 2:** Features a portrait of the Principal and a short "Principal's Message" written by them.
- **Slide 3:** No image. Pure typography detailing college history, vision, and stats (combining previous slide details + extra institutional facts).
- **Hamburger "About" Screen:** Reuses the exact layout and content of Slide 3, but exists as a standalone screen accessible via the hamburger menu, featuring a back arrow at the top left.
- **Navigation:** Top left hamburger menu, top right "Sign Up" and "Login" buttons persist across all slides. Pagination dots indicate the active slide.

## 📌 5. Key Technical Decisions & Constraints
- **Notifications Cache:** 10-minute memory cache prevents hammering the college site.
- **Image Constraints:** Android client compresses to WebP (<500KB). Go enforces a 2MB limit.
- **Memory Management:** Node.js and PHP are installed but idle until future features are built.
