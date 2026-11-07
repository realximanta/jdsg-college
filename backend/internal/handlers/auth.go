package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/realximanta/jdsg-college/backend/internal/auth"
	"github.com/realximanta/jdsg-college/backend/internal/config"
	"github.com/realximanta/jdsg-college/backend/internal/models"
	"github.com/realximanta/jdsg-college/backend/internal/storage"
)

type AuthHandler struct {
	cfg *config.Config
	kv  *storage.KVStore
}

func NewAuthHandler(cfg *config.Config, kv *storage.KVStore) *AuthHandler {
	return &AuthHandler{cfg: cfg, kv: kv}
}

// GoogleLogin verifies the token and either logs the user in or prompts for signup.
func (h *AuthHandler) GoogleLogin(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	var req models.AuthRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	email, name, err := auth.VerifyGoogleToken(r.Context(), req.IDToken, h.cfg.GoogleClientID)
	if err != nil {
		http.Error(w, "Unauthorized: "+err.Error(), http.StatusUnauthorized)
		return
	}

	// Check if user exists in KV
	userData, err := h.kv.Get("user:" + email)
	if err != nil {
		http.Error(w, "Internal server error", http.StatusInternalServerError)
		return
	}

	if userData == nil {
		// User doesn't exist. Generate a temporary JWT so the app can authenticate the /api/users call.
		signupToken, _ := auth.GenerateJWT(email, email, h.cfg.JWTSecret)
		
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(map[string]interface{}{
			"exists": false,
			"email":  email,
			"name":   name,
			"token":  signupToken,
		})
		return
	}

	// User exists, generate JWT and return
	var user models.User
	if err := json.Unmarshal(userData, &user); err != nil {
		http.Error(w, "Corrupted user data", http.StatusInternalServerError)
		return
	}

	token, err := auth.GenerateJWT(user.ID, user.Email, h.cfg.JWTSecret)
	if err != nil {
		http.Error(w, "Failed to generate token", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(models.AuthResponse{
		Token: token,
		User:  user,
	})
}
