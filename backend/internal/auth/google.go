package auth

import (
	"context"
	"errors"
	"fmt"

	"google.golang.org/api/idtoken"
)

// VerifyGoogleToken validates the ID token sent from the Android client.
// It returns the user's email and name if successful.
func VerifyGoogleToken(ctx context.Context, idToken, expectedClientID string) (string, string, error) {
	payload, err := idtoken.Validate(ctx, idToken, expectedClientID)
	if err != nil {
		return "", "", fmt.Errorf("invalid token: %w", err)
	}

	email, ok := payload.Claims["email"].(string)
	if !ok || email == "" {
		return "", "", errors.New("email not found in token claims")
	}

	name, ok := payload.Claims["name"].(string)
	if !ok || name == "" {
		name = "New User" // Fallback if name isn't provided
	}

	return email, name, nil
}
