package config

import (
	"os"
)

type Config struct {
	Port             string
	GoogleClientID   string
	JWTSecret        string
	
	// Cloudflare (Shared)
	CFAccountID      string
	
	// R2 (Object Storage for images)
	R2AccessKey      string
	R2SecretKey      string
	R2BucketName     string
	
	// KV (NoSQL for user metadata)
	CFAPIToken       string
	CFKVNamespaceID  string
}

func Load() *Config {
	return &Config{
		Port:             getEnv("PORT", "8080"),
		GoogleClientID:   os.Getenv("GOOGLE_CLIENT_ID"),
		JWTSecret:        os.Getenv("JWT_SECRET"),
		
		CFAccountID:      os.Getenv("CF_ACCOUNT_ID"),
		
		R2AccessKey:      os.Getenv("R2_ACCESS_KEY_ID"),
		R2SecretKey:      os.Getenv("R2_SECRET_ACCESS_KEY"),
		R2BucketName:     os.Getenv("R2_BUCKET_NAME"),
		
		CFAPIToken:       os.Getenv("CF_API_TOKEN"),
		CFKVNamespaceID:  os.Getenv("CF_KV_NAMESPACE_ID"),
	}
}

func getEnv(key, fallback string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return fallback
}
