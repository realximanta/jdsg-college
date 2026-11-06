# Build stage
FROM golang:1.22-alpine AS builder

WORKDIR /app

# Copy go.mod (and go.sum if you had dependencies, but currently it's empty)
COPY go.mod ./
# RUN go mod download # Uncomment when dependencies are added

# Copy source code
COPY . .

# Build the binary
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o main .

# Final stage
FROM alpine:latest

WORKDIR /app

# Install ca-certificates for HTTPS requests (needed later for Google Auth/R2)
RUN apk --no-cache add ca-certificates

# Copy the binary from builder
COPY --from=builder /app/main .

# Expose port (Render sets PORT env var, default 8080)
EXPOSE 8080

# Run the binary
CMD ["./main"]
