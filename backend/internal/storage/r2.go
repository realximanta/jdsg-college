package storage

import (
	"bytes"
	"context"
	"fmt"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type R2Client struct {
	client  *s3.Client
	bucket  string
	baseURL string // e.g., https://cdn.jdsgcollege.dpdns.org
}

func NewR2Client(accountID, accessKey, secretKey, bucket, baseURL string) (*R2Client, error) {
	r2Resolver := aws.EndpointResolverWithOptionsFunc(func(service, region string, options ...interface{}) (aws.Endpoint, error) {
		return aws.Endpoint{
			URL: fmt.Sprintf("https://%s.r2.cloudflarestorage.com", accountID),
		}, nil
	})

	cfg, err := config.LoadDefaultConfig(context.TODO(),
		config.WithEndpointResolverWithOptions(r2Resolver),
		config.WithCredentialsProvider(credentials.NewStaticCredentialsProvider(accessKey, secretKey, "")),
		config.WithRegion("auto"),
	)
	if err != nil {
		return nil, err
	}

	return &R2Client{
		client:  s3.NewFromConfig(cfg),
		bucket:  bucket,
		baseURL: baseURL,
	}, nil
}

// UploadFile sends an image buffer to R2 and returns the public CDN URL.
func (r *R2Client) UploadFile(ctx context.Context, key string, fileBytes []byte, contentType string) (string, error) {
	_, err := r.client.PutObject(ctx, &s3.PutObjectInput{
		Bucket:      aws.String(r.bucket),
		Key:         aws.String(key),
		Body:        bytes.NewReader(fileBytes),
		ContentType: aws.String(contentType),
	})
	if err != nil {
		return "", fmt.Errorf("failed to upload to R2: %w", err)
	}

	// Construct the final public URL
	publicURL := fmt.Sprintf("%s/%s", strings.TrimSuffix(r.baseURL, "/"), key)
	return publicURL, nil
}
