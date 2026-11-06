package models

import "time"

type User struct {
	ID               string    `json:"id"`
	Email            string    `json:"email"`
	FullName         string    `json:"full_name"`
	Role             string    `json:"role"` // "student" or "teacher"
	
	// Student specific
	Class            string    `json:"class,omitempty"`
	Semester         string    `json:"semester,omitempty"`
	RollNo           string    `json:"roll_no,omitempty"`
	EmergencyContact string    `json:"emergency_contact,omitempty"`

	// Teacher specific
	Department       string    `json:"department,omitempty"`
	Designation      string    `json:"designation,omitempty"`

	// Common fields
	Phone            string    `json:"phone"`
	ProfilePicURL    string    `json:"profile_pic_url"`
	
	CreatedAt        time.Time `json:"created_at"`
	UpdatedAt        time.Time `json:"updated_at"`
}

type AuthRequest struct {
	IDToken string `json:"id_token"`
}

type AuthResponse struct {
	Token string `json:"token"`
	User  User   `json:"user"`
}
