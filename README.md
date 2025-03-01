# 🔒 PII Detector & Redaction App

## 🚀 Overview

PII Detector & Redaction is a powerful tool that allows users to upload **PDFs, images, or plain text** to automatically detect and mask/remove **Personally Identifiable Information (PII)**. It ensures **privacy protection** by using **OCR, NLP models (Spacy/BERT), and Regex-based redaction**.

## ✨ Features

✅ Upload **PDF, Image, or Text** for processing\
✅ **OCR Extraction** for PDFs & Images (Local Tesseract/EasyOCR)\
✅ **PII Detection** using **Spacy/BERT** (Local Machine)\
✅ Highlight detected **PII entities** (e.g., Names, Emails, Phone Numbers)\
✅ Options to **Mask (\*\*\*\*) or Remove** PII\
✅ Download the processed document (PDF/Text)\
✅ **Fully Local Processing** – No data leaves your machine!

## 🏗️ Tech Stack

- **Frontend:** React (Web) / React Native (Mobile)
- **Backend:** FastAPI / Flask (Python-based API)
- **OCR:** Tesseract OCR / EasyOCR
- **PII Detection:** Spacy NER / BERT (fine-tuned for PII)
- **Database:** MongoDB (for logs) / Local Storage
- **Deployment:** Docker (For local OCR & NLP processing)

## 📸 Workflow

1️⃣ **User Uploads** a **PDF, Image, or Text**\
2️⃣ **OCR Processing** (If Image/PDF) extracts the text\
3️⃣ **PII Detection** (Using Spacy/BERT) identifies sensitive data\
4️⃣ **Highlighting** of detected PII in the UI\
5️⃣ User selects **Masking (\*\*\*\*) or Removal**\
6️⃣ **Processed file is available** for download

## 📂 Installation

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/yourusername/pii-detector.git
cd pii-detector
```

### 2️⃣ Backend Setup

```bash
cd backend
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
```

Run the API:

```bash
uvicorn main:app --reload
```

### 3️⃣ Frontend Setup

```bash
cd frontend
npm install
npm start
```

## 🎯 API Endpoints

| Method | Endpoint    | Description                   |
| ------ | ----------- | ----------------------------- |
| `POST` | `/upload`   | Uploads file (PDF/Image/Text) |
| `GET`  | `/process`  | Runs OCR & PII Detection      |
| `POST` | `/redact`   | Redacts/masks PII             |
| `GET`  | `/download` | Returns processed file        |

## 🖥️ Demo

🚧 *Coming Soon!* 🚧

## 🤝 Contributions

Want to improve this project? Feel free to fork and submit a PR! 💙

## 📜 License

MIT License © 2025 Your Name

---

⭐ **Star this repo** if you find it useful!

