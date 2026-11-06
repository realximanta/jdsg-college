package storage

import (
	"bytes"
	"fmt"
	"io"
	"net/http"
)

type KVStore struct {
	accountID   string
	namespaceID string
	apiToken    string
	httpClient  *http.Client
}

func NewKVStore(accountID, namespaceID, apiToken string) *KVStore {
	return &KVStore{
		accountID:   accountID,
		namespaceID: namespaceID,
		apiToken:    apiToken,
		httpClient:  &http.Client{},
	}
}

// Put writes data to Cloudflare Workers KV using the REST API.
func (k *KVStore) Put(key string, value []byte) error {
	url := fmt.Sprintf("https://api.cloudflare.com/client/v4/accounts/%s/storage/kv/namespaces/%s/values/%s", k.accountID, k.namespaceID, key)
	
	req, err := http.NewRequest(http.MethodPut, url, bytes.NewBuffer(value))
	if err != nil {
		return err
	}
	
	req.Header.Set("Authorization", "Bearer "+k.apiToken)
	req.Header.Set("Content-Type", "application/json")

	resp, err := k.httpClient.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("KV PUT failed: %s - %s", resp.Status, string(body))
	}
	return nil
}

// Get retrieves data from Cloudflare Workers KV.
func (k *KVStore) Get(key string) ([]byte, error) {
	url := fmt.Sprintf("https://api.cloudflare.com/client/v4/accounts/%s/storage/kv/namespaces/%s/values/%s", k.accountID, k.namespaceID, key)
	
	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}
	
	req.Header.Set("Authorization", "Bearer "+k.apiToken)

	resp, err := k.httpClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusNotFound {
		return nil, nil // Return nil if key doesn't exist
	}
	
	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("KV GET failed: %s - %s", resp.Status, string(body))
	}

	return io.ReadAll(resp.Body)
}
