package config

import (
	"os"
)

type Config struct {
	Port           string
	GoogleClientID string
	JWTSecret      string
	R2AccountID    string
	R2AccessKey    string
	R2SecretKey    string
	R2BucketName   string
}

func Load() *Config {
	return &Config{
		Port:           getEnv("PORT", "8080"),
		GoogleClientID: os.Getenv("GOOGLE_CLIENT_ID"),
		JWTSecret:      os.Getenv("JWT_SECRET"),
		R2AccountID:    os.Getenv("R2_ACCOUNT_ID"),
		R2AccessKey:    os.Getenv("R2_ACCESS_KEY_ID"),
		R2SecretKey:    os.Getenv("R2_SECRET_ACCESS_KEY"),
		R2BucketName:   os.Getenv("R2_BUCKET_NAME"),
	}
}

func getEnv(key, fallback string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return fallback
}
