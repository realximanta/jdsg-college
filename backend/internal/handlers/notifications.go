package handlers

import (
	"encoding/json"
	"net/http"
	"os/exec"
	"sync"
	"time"
)

type Notification struct {
	Title string `json:"title"`
	URL   string `json:"url"`
	Date  string `json:"date"`
}

type NotificationHandler struct {
	mu          sync.RWMutex
	cache       []Notification
	lastFetched time.Time
}

func NewNotificationHandler() *NotificationHandler {
	return &NotificationHandler{}
}

// GetNotifications executes the Python scraper and caches the result for 10 minutes.
func (h *NotificationHandler) GetNotifications(w http.ResponseWriter, r *http.Request) {
	h.mu.RLock()
	if time.Since(h.lastFetched) < 10*time.Minute && len(h.cache) > 0 {
		defer h.mu.RUnlock()
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(h.cache)
		return
	}
	h.mu.RUnlock()

	// Execute Python script (Path is relative to the Docker WORKDIR)
	cmd := exec.Command("python3", "scripts/scraper.py")
	out, err := cmd.Output()
	if err != nil {
		// If Python script fails, return stale cache if available
		h.mu.RLock()
		defer h.mu.RUnlock()
		if len(h.cache) > 0 {
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(h.cache)
			return
		}
		http.Error(w, "Failed to run scraper", http.StatusInternalServerError)
		return
	}

	var notifications []Notification
	if err := json.Unmarshal(out, &notifications); err != nil {
		// Parse error fallback
		h.mu.RLock()
		defer h.mu.RUnlock()
		if len(h.cache) > 0 {
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(h.cache)
			return
		}
		http.Error(w, "Failed to parse scraper output", http.StatusInternalServerError)
		return
	}

	// Update cache
	h.mu.Lock()
	h.cache = notifications
	h.lastFetched = time.Now()
	h.mu.Unlock()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(notifications)
}
