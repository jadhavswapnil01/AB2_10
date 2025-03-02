import re
import json
import tkinter as tk
from tkinter import filedialog

def upload_file():
    """Open file selection dialog."""
    root = tk.Tk()
    root.withdraw()
    root.attributes('-topmost', True)  # Ensure dialog is on top
    file_path = filedialog.askopenfilename(
        title="Select a File",
        filetypes=[("Text Files", "*.txt"), ("All Files", "*.*")]
    )
    if not file_path:
        print("No file selected. Exiting...")
        exit(1)
    return file_path

# Regex for card number & expiry date
CARD_PATTERNS = {
    "CARD_NUMBER": r"\b(?:\d{4}[-\s]?){3}\d{4}\b",  # Handles spaces & dashes
    "CARD_EXPIRY": r"\b(0[1-9]|1[0-2])\s*/\s*(\d{2}|\d{4})\b"  # MM/YY or MM/YYYY
}

def read_text_file(file_path):
    """Read file contents."""
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            return file.read()
    except Exception as e:
        print("Error reading file:", e)
        return ""

def detect_card_info(text):
    """Extract card numbers & expiry dates."""
    card_data = {}
    for label, pattern in CARD_PATTERNS.items():
        matches = re.findall(pattern, text)
        if matches:
            card_data[label] = matches if len(matches) > 1 else matches[0]
    return card_data

if __name__ == "__main__":
    file_path = upload_file()
    extracted_text = read_text_file(file_path)
    detected_cards = detect_card_info(extracted_text)

    print("\nDetected Card Info (JSON Format):\n", json.dumps(detected_cards, indent=4))
