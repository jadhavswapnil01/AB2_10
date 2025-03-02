import re
import json
import tkinter as tk
from tkinter import filedialog

def upload_file():
    """Open file selection dialog."""
    root = tk.Tk()
    root.withdraw()
    root.attributes('-topmost', True)  # Ensure dialog appears on top
    file_path = filedialog.askopenfilename(
        title="Select a File",
        filetypes=[("Text Files", "*.txt"), ("All Files", "*.*")]
    )
    if not file_path:
        print("No file selected. Exiting...")
        exit(1)
    return file_path

# Updated regex patterns (Only Aadhaar, PAN, DOB, and Name)
PII_PATTERNS = {
    "AADHAAR": r"\b\d{4}\s*\d{4}\s*\d{4}\b",  # Aadhaar (Handles spaces dynamically)
    "PAN": r"\b[A-Z]{5}\d{4}[A-Z]\b",  # PAN (Always 10 characters)
    "DOB": r"\b\d{2}\s*[-/]\s*\d{2}\s*[-/]\s*\d{4}\b",  # Date of Birth (DD/MM/YYYY or DD-MM-YYYY)
    "NAME": r"\b[A-Z][a-z]+(?:\s[A-Z][a-z]+)+(?:\s[A-Z][a-z]+)?\b"  # Name (First, Middle, Last)
}

def read_text_file(file_path):
    """Read text from a .txt file."""
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            return file.read()
    except Exception as e:
        print("Error reading file:", e)
        return ""

def detect_pii(text):
    """Detect PII using regex and return structured JSON data."""
    pii_data = {}  # Dictionary to store extracted PII
    for label, pattern in PII_PATTERNS.items():
        matches = re.findall(pattern, text)
        if matches:
            pii_data[label] = matches if len(matches) > 1 else matches[0]  # Store single or multiple values
    return pii_data

if __name__ == "__main__":
    file_path = upload_file()
    extracted_text = read_text_file(file_path)
    detected_pii = detect_pii(extracted_text)

    print("\nExtracted PII (JSON Format):\n", json.dumps(detected_pii, indent=4))
