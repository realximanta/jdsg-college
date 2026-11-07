#!/usr/bin/env python3
import sys
import json
import requests
from bs4 import BeautifulSoup
from urllib.parse import urljoin

def log_error(message):
    # Print to stderr so Go can capture it, without corrupting the JSON on stdout
    print(message, file=sys.stderr)

def main():
    base_url = "https://jdsgcollege.edu.in/"
    target_url = urljoin(base_url, "category/notifications/")
    
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
    
    try:
        response = requests.get(target_url, headers=headers, timeout=15)
        response.raise_for_status()
    except requests.exceptions.RequestException as e:
        log_error(f"Failed to fetch {target_url}: {e}")
        sys.exit(1)

    try:
        soup = BeautifulSoup(response.text, 'html.parser')
        results = []

        # Find articles. Using broad class matching to survive theme updates.
        articles = soup.find_all(['article', 'div'], class_=['post', 'type-post', 'category-notifications'])
        
        if not articles:
            log_error("No articles found. HTML structure might have changed.")
            # Output empty array so the Go cache can still be updated (even if empty)
            print(json.dumps([]))
            sys.exit(0)

        for article in articles:
            # 1. Extract Title and URL
            title_elem = article.find('h2')
            if not title_elem:
                title_elem = article.find('h3')
                
            if not title_elem:
                continue
                
            link_elem = title_elem.find('a')
            if not link_elem or not link_elem.get('href'):
                continue
                
            title = link_elem.text.strip()
            # Convert relative URLs (e.g., /2026/07/) to absolute
            link = urljoin(base_url, link_elem.get('href', ''))
            
            # 2. Extract Date
            date_elem = article.find('time') or article.find(class_=['entry-date', 'posted-on'])
            date = date_elem.text.strip() if date_elem else "Unknown Date"

            if title and link:
                results.append({
                    "title": title,
                    "url": link,
                    "date": date
                })

        # Output valid JSON to stdout for the Go backend to consume
        print(json.dumps(results))

    except Exception as e:
        log_error(f"Parsing error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
