package main

import (
	"fmt"
	"log"
	"net/http"
	"os"
	"time"

	"github.com/realximanta/jdsg-college/backend/internal/config"
	"github.com/realximanta/jdsg-college/backend/internal/handlers"
	"github.com/realximanta/jdsg-college/backend/internal/storage"
)

func main() {
	cfg := config.Load()

	// Initialize Storage Clients
	kvStore := storage.NewKVStore(cfg.CFAccountID, cfg.CFKVNamespaceID, cfg.CFAPIToken)
	
	r2Client, err := storage.NewR2Client(cfg.CFAccountID, cfg.R2AccessKey, cfg.R2SecretKey, cfg.R2BucketName, "https://cdn.jdsgcollege.dpdns.org")
	if err != nil {
		log.Fatalf("Failed to initialize R2 client: %v", err)
	}

	// Initialize Handlers
	authHandler := handlers.NewAuthHandler(cfg, kvStore)
	userHandler := handlers.NewUserHandler(cfg, kvStore, r2Client)
	notifHandler := handlers.NewNotificationHandler()

	// Set up Router
	mux := http.NewServeMux()

	// Health Check for UptimeRobot
	liveHandler := func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		if r.Method == http.MethodGet {
			w.Write([]byte("OK"))
		}
	}
	mux.HandleFunc("GET /live", liveHandler)
	mux.HandleFunc("HEAD /live", liveHandler)

	// Auth Routes
	mux.HandleFunc("POST /api/auth/google", authHandler.GoogleLogin)

	// User Routes
	mux.HandleFunc("POST /api/users", userHandler.CompleteSignup)
	mux.HandleFunc("GET /api/users/me", userHandler.GetProfile)
	mux.HandleFunc("PUT /api/users/me", userHandler.UpdateProfile)
	mux.HandleFunc("POST /api/users/me/avatar", userHandler.UploadAvatar)

	// Notification Routes
	mux.HandleFunc("GET /api/notifications", notifHandler.GetNotifications)

	// Server config
	srv := &http.Server{
		Addr:         fmt.Sprintf(":%s", cfg.Port),
		Handler:      mux, // We will wrap this with CORS middleware next
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 10 * time.Second,
		IdleTimeout:  15 * time.Second,
	}

	log.Printf("JDSG Backend starting on port %s", cfg.Port)
	if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		log.Fatalf("Server failed: %v", err)
	}
}
