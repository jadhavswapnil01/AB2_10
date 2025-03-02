import re
import json
import sys

# Improved regex patterns for PII detection
PII_PATTERNS = {
    "AADHAAR": r"\b\d{4}\s*\d{4}\s*\d{4}\b",  # Aadhaar (Handles spaces dynamically)
    "PAN": r"\b[A-Z]{5}\d{4}[A-Z]\b",  # PAN (Always 10 characters)
    "MOBILE": r"\b\d{10}\b",  # Mobile (10 digits)
    "DRIVING_LICENSE": r"\b[A-Z]{2}\s*\d{2,4}\s*\d{4,7}\b|\b\d{2}\s*[A-Z]{2}\s*\d{6,9}\b",  # Multiple formats
    "NAME": r"\b[A-Z][a-z]+(?:\s[A-Z][a-z]+)+(?:\s[A-Z][a-z]+)?\b",  # Name (First, Middle, Last)
    "CARD_NUMBER": r"\b(?:\d{4}[-\s]?){3}\d{4}\b",  # Credit/Debit Card (16-digit with spaces/dashes)
    "CARD_EXPIRY": r"\b(0[1-9]|1[0-2])\s*/\s*(\d{2}|\d{4})\b",  # Expiry Date (MM/YY or MM/YYYY)
    "DOB": r"\b\d{2}\s*[-/]\s*\d{2}\s*[-/]\s*\d{4}\b",  # Date of Birth (DD/MM/YYYY or DD-MM-YYYY)
    "GMAIL": r"\b[A-Za-z0-9._%+-]+@gmail\.com\b"  # Gmail ID detection
}

def read_text_file(file_path):
    """Read text from a file."""
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            return file.read()
    except Exception as e:
        print("Error reading file:", e)
        sys.exit(1)

def detect_pii(text):
    """Detect PII using regex and return structured JSON data."""
    pii_data = {}  # Dictionary to store extracted PII
    for label, pattern in PII_PATTERNS.items():
        matches = re.findall(pattern, text)
        if matches:
            pii_data[label] = matches if len(matches) > 1 else matches[0]  # Store single or multiple values
    return pii_data

# Step 1: Get file path from command line argument
if len(sys.argv) < 2:
    print("Usage: python script.py <file_path>")
    sys.exit(1)

file_path = sys.argv[1]

# Step 2: Process the file
extracted_text = read_text_file(file_path)
detected_pii = detect_pii(extracted_text)

# Step 3: Print the results
print("\nExtracted PII (JSON Format):\n", json.dumps(detected_pii, indent=4))
