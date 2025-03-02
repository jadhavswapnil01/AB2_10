import re
import json
import tkinter as tk
from tkinter import filedialog

def upload_file():
    """Forcefully open a file selection dialog and return the chosen file path."""
    root = tk.Tk()
    root.withdraw()  # Hide the root window

    # Make sure the dialog always appears in focus
    root.attributes('-topmost', True)
    
    file_path = filedialog.askopenfilename(
        title="Select a File",
        filetypes=[("Text Files", "*.txt"), ("All Files", "*.*")]
    )

    if not file_path:
        print("No file selected. Exiting...")
        exit(1)
    
    return file_path

# Updated regex pattern for Driving License detection (MH 12 20240001634 format)
DL_PATTERN = r"\b[A-Z]{2}\s*\d{2}\s*\d{6,12}\b"

def read_text_file(file_path):
    """Read text content from the selected file."""
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            return file.read()
    except Exception as e:
        print("Error reading file:", e)
        return ""

def detect_driving_license(text):
    """Find and extract driving license numbers from text."""
    matches = re.findall(DL_PATTERN, text)
    return {"Driving_License_Number": matches if matches else "Not Found"}

if __name__ == "__main__":
    # This ensures the file selection dialog ALWAYS appears
    file_path = upload_file()

    # Read and process the uploaded file
    extracted_text = read_text_file(file_path)
    detected_license = detect_driving_license(extracted_text)

    # Print results
    print("\nDetected Driving License (JSON Format):\n", json.dumps(detected_license, indent=4))
