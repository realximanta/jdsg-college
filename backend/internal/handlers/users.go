package handlers

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"time"

	"github.com/realximanta/jdsg-college/backend/internal/auth"
	"github.com/realximanta/jdsg-college/backend/internal/config"
	"github.com/realximanta/jdsg-college/backend/internal/models"
	"github.com/realximanta/jdsg-college/backend/internal/storage"
)

type UserHandler struct {
	cfg *config.Config
	kv  *storage.KVStore
	r2  *storage.R2Client
}

func NewUserHandler(cfg *config.Config, kv *storage.KVStore, r2 *storage.R2Client) *UserHandler {
	return &UserHandler{cfg: cfg, kv: kv, r2: r2}
}

// Helper to extract email from the Authorization header
func (h *UserHandler) getAuthEmail(r *http.Request) (string, error) {
	authHeader := r.Header.Get("Authorization")
	if authHeader == "" || !strings.HasPrefix(authHeader, "Bearer ") {
		return "", fmt.Errorf("missing or invalid token")
	}
	tokenString := strings.TrimPrefix(authHeader, "Bearer ")
	return auth.VerifyJWT(tokenString, h.cfg.JWTSecret)
}

// CompleteSignup finalizes the user profile after Google Auth.
func (h *UserHandler) CompleteSignup(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	email, err := h.getAuthEmail(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}

	var user models.User
	if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// Enforce constraints
	if len(user.Phone) != 10 {
		http.Error(w, "Phone number must be exactly 10 digits", http.StatusBadRequest)
		return
	}
	if user.ProfilePicURL == "" {
		http.Error(w, "Profile picture is required", http.StatusBadRequest)
		return
	}

	user.ID = email // Use email as unique ID for now
	user.Email = email
	user.CreatedAt = time.Now()
	user.UpdatedAt = time.Now()

	data, err := json.Marshal(user)
	if err != nil {
		http.Error(w, "Internal server error", http.StatusInternalServerError)
		return
	}

	if err := h.kv.Put("user:"+email, data); err != nil {
		http.Error(w, "Failed to save user", http.StatusInternalServerError)
		return
	}

	// Generate fresh JWT with the new user ID
	token, _ := auth.GenerateJWT(user.ID, user.Email, h.cfg.JWTSecret)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(models.AuthResponse{
		Token: token,
		User:  user,
	})
}

// GetProfile returns the authenticated user's profile.
func (h *UserHandler) GetProfile(w http.ResponseWriter, r *http.Request) {
	email, err := h.getAuthEmail(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}

	userData, err := h.kv.Get("user:" + email)
	if err != nil || userData == nil {
		http.Error(w, "User not found", http.StatusNotFound)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.Write(userData)
}

// UpdateProfile overwrites the user's profile fields.
func (h *UserHandler) UpdateProfile(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPut {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	email, err := h.getAuthEmail(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}

	var user models.User
	if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	if len(user.Phone) != 10 {
		http.Error(w, "Phone number must be exactly 10 digits", http.StatusBadRequest)
		return
	}

	user.Email = email
	user.UpdatedAt = time.Now()

	data, err := json.Marshal(user)
	if err != nil {
		http.Error(w, "Internal server error", http.StatusInternalServerError)
		return
	}

	if err := h.kv.Put("user:"+email, data); err != nil {
		http.Error(w, "Failed to update user", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.Write(data)
}

// UploadAvatar receives a multipart image, uploads to R2, and updates KV.
func (h *UserHandler) UploadAvatar(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	email, err := h.getAuthEmail(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}

	// Limit upload size to 2MB (client should compress, but server protects itself)
	r.Body = http.MaxBytesReader(w, r.Body, 2<<20)
	if err := r.ParseMultipartForm(2 << 20); err != nil {
		http.Error(w, "File too large or invalid form", http.StatusBadRequest)
		return
	}

	file, _, err := r.FormFile("avatar")
	if err != nil {
		http.Error(w, "Missing avatar file", http.StatusBadRequest)
		return
	}
	defer file.Close()

	fileBytes, err := io.ReadAll(file)
	if err != nil {
		http.Error(w, "Failed to read file", http.StatusInternalServerError)
		return
	}

	// Generate unique R2 key
	key := fmt.Sprintf("users/%s/avatar.webp", strings.ReplaceAll(email, "@", "_"))
	
	// Upload to R2
	publicURL, err := h.r2.UploadFile(context.Background(), key, fileBytes, "image/webp")
	if err != nil {
		http.Error(w, "Failed to upload image", http.StatusInternalServerError)
		return
	}

	// Update user profile in KV
	userData, err := h.kv.Get("user:" + email)
	if err != nil || userData == nil {
		http.Error(w, "User not found", http.StatusNotFound)
		return
	}

	var user models.User
	json.Unmarshal(userData, &user)
	user.ProfilePicURL = publicURL
	user.UpdatedAt = time.Now()

	updatedData, _ := json.Marshal(user)
	h.kv.Put("user:"+email, updatedData)

	w.Header().Set("Content-Type", "application/json")
	w.Write(updatedData)
}
