import os
import sys

# Install missing dependencies if not found
try:
    import fitz  # PyMuPDF for PDFs
    import pytesseract  # OCR for images
    from PIL import Image  # Image processing
except ImportError:
    print("⚠ Installing required libraries... Please wait.")
    os.system("pip install pymupdf pytesseract pillow")

    # Import again after installation
    import fitz
    import pytesseract
    from PIL import Image

# ✅ Set up Tesseract OCR path for Windows users (update if needed)
pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

def extract_text_from_pdf(pdf_path):
    """Extracts text from a selectable PDF."""
    try:
        doc = fitz.open(pdf_path)
        extracted_text = "\n".join([page.get_text("text") for page in doc])

        if not extracted_text.strip():  # If no text is found, try OCR
            print("🔍 No selectable text found in PDF. Attempting OCR...")
            extracted_text = extract_text_from_scanned_pdf(pdf_path)
        
        return extracted_text if extracted_text.strip() else None
    except Exception as e:
        print(f"⚠ Error reading PDF: {e}")
        return None

def extract_text_from_scanned_pdf(pdf_path):
    """Uses OCR to extract text from scanned PDFs."""
    text = ""
    try:
        doc = fitz.open(pdf_path)
        for page_num in range(len(doc)):
            img = doc[page_num].get_pixmap()  # Convert PDF page to image
            img_pil = Image.frombytes("RGB", [img.width, img.height], img.samples)
            text += pytesseract.image_to_string(img_pil) + "\n"
        return text.strip()
    except Exception as e:
        print(f"⚠ Error performing OCR on scanned PDF: {e}")
        return None

def extract_text_from_image(image_path):
    """Extracts text from an image using OCR."""
    try:
        img = Image.open(image_path)
        extracted_text = pytesseract.image_to_string(img)
        return extracted_text.strip() if extracted_text else None
    except Exception as e:
        print(f"⚠ Error reading image: {e}")
        return None

if __name__ == "__main__":
    # Check if a file path is provided as an argument
    if len(sys.argv) != 2:
        print("❌ Usage: python script.py <file_path>")
        sys.exit(1)

    file_path = sys.argv[1]
    
    if not os.path.exists(file_path):
        print(f"❌ File not found: {file_path}")
        sys.exit(1)

    file_extension = os.path.splitext(file_path)[-1].lower()
    output_txt_path = "output.txt"
    extracted_text = None

    # Step 2: Detect file type and extract text
    if file_extension == ".pdf":
        extracted_text = extract_text_from_pdf(file_path)
    elif file_extension in [".jpg", ".jpeg", ".png", ".tiff", ".bmp", ".gif"]:
        extracted_text = extract_text_from_image(file_path)
    else:
        print("❌ Unsupported file format.")
        sys.exit(1)

    # Step 3: Save extracted text to a file
    if extracted_text:
        with open(output_txt_path, "w", encoding="utf-8") as txt_file:
            txt_file.write(extracted_text)
        print(f"✅ Text extracted and saved to: {output_txt_path}")
    else:
        print("❌ No text extracted.")
