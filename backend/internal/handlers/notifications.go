package handlers

import (
	"encoding/json"
	"net/http"
	"sync"
	"time"

	"github.com/PuerkitoBio/goquery"
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

// GetNotifications scrapes the college site, caches the result for 10 minutes.
func (h *NotificationHandler) GetNotifications(w http.ResponseWriter, r *http.Request) {
	h.mu.RLock()
	if time.Since(h.lastFetched) < 10*time.Minute && len(h.cache) > 0 {
		defer h.mu.RUnlock()
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(h.cache)
		return
	}
	h.mu.RUnlock()

	// Fetch fresh data
	notifications, err := h.scrape()
	if err != nil {
		// If scrape fails, return cache if available
		if len(h.cache) > 0 {
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(h.cache)
			return
		}
		http.Error(w, "Failed to fetch notifications", http.StatusInternalServerError)
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

func (h *NotificationHandler) scrape() ([]Notification, error) {
	res, err := http.Get("https://jdsgcollege.edu.in/category/notifications/")
	if err != nil {
		return nil, err
	}
	defer res.Body.Close()

	if res.StatusCode != 200 {
		return nil, err
	}

	doc, err := goquery.NewDocumentFromReader(res.Body)
	if err != nil {
		return nil, err
	}

	var results []Notification

	// Note: The CSS selectors (.post, h2 a) are educated guesses.
	// We will verify the exact HTML structure of the college site later if it fails.
	doc.Find(".post").Each(func(i int, s *goquery.Selection) {
		title := s.Find("h2 a").Text()
		url, _ := s.Find("h2 a").Attr("href")
		date := s.Find(".entry-date").Text() // or whatever the date class is

		if title != "" && url != "" {
			results = append(results, Notification{
				Title: title,
				URL:   url,
				Date:  date,
			})
		}
	})

	return results, nil
}
