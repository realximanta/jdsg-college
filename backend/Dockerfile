# Build stage
FROM golang:1.22-alpine AS builder

WORKDIR /app

# Copy everything and tidy to resolve go.sum and dependencies
COPY . .
RUN go mod tidy
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o main .

# Final stage
FROM alpine:3.19

WORKDIR /app

# Install CA certs for HTTPS and Python with required libraries
RUN apk --no-cache add ca-certificates python3 py3-requests py3-beautifulsoup4

# Copy the compiled Go binary
COPY --from=builder /app/main .

# Copy the Python scripts directory
COPY --from=builder /app/scripts ./scripts

# Expose port (Render sets PORT env var)
EXPOSE 8080

# Run the Go server
CMD ["./main"]
