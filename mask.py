import json
import fitz  # PyMuPDF for handling PDF
import re
import sys
import os

def load_pii_json():
    """Load the PII JSON file generated from general.py."""
    try:
        with open("C:\\Users\\swapn\\hackethon\\AB2_10\\pii_data.json", "r", encoding="utf-8") as json_file:
            return json.load(json_file)
    except Exception as e:
        print("Error loading PII data:", e)
        exit(1)

def mask_pii_in_pdf(pdf_path):
    """Redacts/masks the detected PII in the PDF."""
    try:
        pii_data = load_pii_json()
        doc = fitz.open(pdf_path)

        # Convert PII values to a list of strings for searching
        pii_values = []
        for key, value in pii_data.items():
            if isinstance(value, list):
                pii_values.extend(value)  # Add multiple values (e.g., mobile numbers)
            else:
                pii_values.append(value)

        # Process each page of the PDF
        for page in doc:
            text = page.get_text("text")  # Extract text from the page

            for pii in pii_values:
                # Search for each PII data in the text
                matches = [m for m in re.finditer(re.escape(pii), text)]
                
                for match in matches:
                    rect = page.search_for(match.group())  # Locate text in PDF
                    
                    for r in rect:
                        page.add_redact_annot(r, fill=(0, 0, 0))  # Add black mask
                    
                    page.apply_redactions()  # Apply the redaction permanently

        # Save the masked PDF
        output_pdf_path = pdf_path.replace(".pdf", "_masked.pdf")
        doc.save(output_pdf_path)
        doc.close()

        print(output_pdf_path)  # Return the masked PDF path
    except Exception as e:
        print("Error processing PDF:", e)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python mask.py <pdf_file_path>")
        exit(1)

    pdf_file_path = sys.argv[1]
    mask_pii_in_pdf(pdf_file_path)
