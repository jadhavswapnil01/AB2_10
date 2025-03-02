import re
import json
import os

# Default file path
DEFAULT_FILE_PATH = r"C:\Users\swapn\hackethon\AB2_10\output.txt"

# Regex patterns for different types of personal data
PII_PATTERNS = {
    "AADHAAR": r"\b\d{4}\s*\d{4}\s*\d{4}\b",  # Aadhaar with flexible spaces
    "PAN": r"\b[A-Z]{5}\d{4}[A-Z]\b",  # PAN format: ABCDE1234F
    "MOBILE": r"\b\d{10}\b",  # 10-digit mobile number
    "DRIVING_LICENSE": r"\b[A-Z]{2}\s*\d{2}\s*\d{6,12}\b",  # MH 12 20240001634 format
    "CARD_NUMBER": r"\b(?:\d{4}[-\s]?){3}\d{4}\b",  # Credit/Debit card
    "CARD_EXPIRY": r"\b(0[1-9]|1[0-2])\s*/\s*(\d{2}|\d{4})\b",  # Expiry MM/YY or MM/YYYY
    "DOB": r"\b\d{2}\s*[-/]\s*\d{2}\s*[-/]\s*\d{4}\b",  # DOB: DD/MM/YYYY or DD-MM-YYYY
    "GMAIL": r"\b[A-Za-z0-9._%+-]+@gmail\.com\b"  # Gmail ID
}

def read_text_file(file_path):
    """Read file contents."""
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            return file.read()
    except Exception as e:
        print(f"⚠ Error reading file {file_path}: {e}")
        return ""

def detect_pii(text):
    """Extract personal data and classify it."""
    pii_data = {}
    for label, pattern in PII_PATTERNS.items():
        matches = re.findall(pattern, text)
        if matches:
            pii_data[label] = matches if len(matches) > 1 else matches[0]
    return pii_data

def save_pii_json(pii_data, output_path):
    """Save detected PII to a JSON file."""
    try:
        os.makedirs(os.path.dirname(output_path), exist_ok=True)  # Ensure directory exists
        with open(output_path, "w", encoding="utf-8") as json_file:
            json.dump(pii_data, json_file, indent=4)
        print(f"\nExtracted PII saved to: {output_path}")
    except Exception as e:
        print(f"⚠ Error saving JSON file: {e}")

if __name__ == "__main__":
    file_path = DEFAULT_FILE_PATH  # Automatically use the default file path
    if not os.path.exists(file_path):
        print(f"⚠ File not found: {file_path}")
        exit(1)

    extracted_text = read_text_file(file_path)
    detected_pii = detect_pii(extracted_text)

    output_json_path = r"C:\Users\swapn\hackethon\AB2_10\pii_data.json"
    save_pii_json(detected_pii, output_json_path)
