# Build stage
FROM golang:1.26-alpine AS builder

WORKDIR /app

# Copy everything and tidy to resolve go.sum and dependencies
COPY . .
RUN go mod tidy
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o main .

# Final stage
FROM alpine:3.19

WORKDIR /app

# Install CA certs, Python (with scraper libs), Node.js, and PHP
RUN apk --no-cache add \
    ca-certificates \
    python3 py3-requests py3-beautifulsoup4 \
    nodejs npm \
    php81 php81-curl php81-openssl

# Copy the compiled Go binary
COPY --from=builder /app/main .

# Copy the Python scripts directory
COPY --from=builder /app/scripts ./scripts

# Expose port
EXPOSE 8080

# Run the Go server
CMD ["./main"]
