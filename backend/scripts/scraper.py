#!/usr/bin/env python3
import sys
import json
import requests
from bs4 import BeautifulSoup

def main():
    url = "https://jdsgcollege.edu.in/category/notifications/"
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
    
    try:
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
    except Exception as e:
        # Exit with error code so Go knows it failed
        sys.exit(1)

    soup = BeautifulSoup(response.text, 'html.parser')
    results = []

    # Standard WordPress article/post selection
    for article in soup.find_all(['article', 'div'], class_=['post', 'type-post']):
        # Find title and URL
        title_elem = article.find('h2')
        if not title_elem:
            continue
            
        link_elem = title_elem.find('a')
        if not link_elem:
            continue
            
        title = link_elem.text.strip()
        link = link_elem.get('href', '')
        
        # Find date
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

if __name__ == "__main__":
    main()
