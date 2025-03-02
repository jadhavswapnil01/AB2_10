import os
import sys
import fitz  # PyMuPDF for PDFs
import pytesseract  # OCR for images
from PIL import Image  # Image processing
import ocrmypdf  # OCR for making PDFs searchable

# Set Tesseract path
pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

def make_pdf_searchable(input_pdf, output_pdf):
    """Applies OCR to a PDF and makes it searchable."""
    try:
        ocrmypdf.ocr(
            input_pdf, 
            output_pdf, 
            force_ocr=True,  # Ensure OCR is applied
            optimize=1,  # Optimize file size
            tesseract_oem=3,  # Use Legacy + LSTM OCR for better accuracy
            tesseract_pagesegmode=6,  # Assume a single uniform block of text
            image_dpi=300  # Improve OCR quality
        )
        return output_pdf
    except Exception as e:
        print(f"Error making PDF searchable: {e}")
        return None


def extract_text_from_pdf(pdf_path):
    """Extracts text from a selectable PDF."""
    try:
        doc = fitz.open(pdf_path)
        extracted_text = "\n".join([page.get_text("text") for page in doc])

        if not extracted_text.strip():  # If no text is found, try OCR
            print("No selectable text found in PDF. Making it searchable...")
            searchable_pdf = pdf_path.replace(".pdf", "_searchable.pdf")
            searchable_pdf = make_pdf_searchable(pdf_path, searchable_pdf)

            if searchable_pdf:
                print("OCR completed. Extracting text from searchable PDF...")
                return extract_text_from_pdf(searchable_pdf)
            else:
                return None
        return extracted_text
    except Exception as e:
        print(f"Error reading PDF: {e}")
        return None

def extract_text_from_image(image_path):
    """Extracts text from an image using OCR."""
    try:
        img = Image.open(image_path)
        extracted_text = pytesseract.image_to_string(img)
        return extracted_text.strip() if extracted_text else None
    except Exception as e:
        print(f"Error reading image: {e}")
        return None

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <file_path>")
        sys.exit(1)

    file_path = sys.argv[1]
    
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        sys.exit(1)

    file_extension = os.path.splitext(file_path)[-1].lower()
    output_txt_path = "C:\\Users\\swapn\\hackethon\\AB2_10\\output.txt"
    extracted_text = None

    if file_extension == ".pdf":
        extracted_text = extract_text_from_pdf(file_path)
    elif file_extension in [".jpg", ".jpeg", ".png", ".tiff", ".bmp", ".gif"]:
        extracted_text = extract_text_from_image(file_path)
    else:
        print("Unsupported file format.")
        sys.exit(1)

    if extracted_text:
        with open(output_txt_path, "w", encoding="utf-8") as txt_file:
            txt_file.write(extracted_text)
        print(f"Text extracted and saved to: {output_txt_path}")
    else:
        print("No text extracted.")
