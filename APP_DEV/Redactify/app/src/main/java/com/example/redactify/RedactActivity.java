package com.example.redactify;

/*import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class RedactActivity extends AppCompatActivity {

    private Uri pdfUri;
    private ImageView pdfPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact);

        TextView tvFileName = findViewById(R.id.tvFileName);
        pdfPreview = findViewById(R.id.pdfPreview);
        Button btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pdfUri")) {
            pdfUri = Uri.parse(intent.getStringExtra("pdfUri"));
            tvFileName.setText("Selected PDF: " + pdfUri.getLastPathSegment());
            displayPdfPreview();
        }

        btnDownloadPdf.setOnClickListener(v -> savePdfToDownloads());
    }

    private void displayPdfPreview() {
        try {
            ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(pdfUri, "r");
            if (descriptor != null) {
                PdfRenderer renderer = new PdfRenderer(descriptor);
                PdfRenderer.Page page = renderer.openPage(0);

                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                pdfPreview.setImageBitmap(bitmap);

                page.close();
                renderer.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void savePdfToDownloads() {
        try {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, "redacted_pdf.pdf");
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");

            Uri outputUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (outputUri != null) {
                try (InputStream inputStream = getContentResolver().openInputStream(pdfUri);
                     OutputStream outputStream = resolver.openOutputStream(outputUri)) {
                    if (inputStream != null && outputStream != null) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        Toast.makeText(this, "PDF Downloaded Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}*/

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RedactActivity extends AppCompatActivity {

    private Uri fileUri;
    private ImageView imagePreview;
    private TextView tvFileName, textPreview;
    private String fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact);

        tvFileName = findViewById(R.id.tvFileName);
        imagePreview = findViewById(R.id.imagePreview);
        textPreview = findViewById(R.id.textPreview);
        Button btnDownload = findViewById(R.id.btnDownload);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fileUri")) {
            fileUri = Uri.parse(intent.getStringExtra("fileUri"));
            fileType = getContentResolver().getType(fileUri);
            tvFileName.setText("Selected File: " + fileUri.getLastPathSegment());
            displayFilePreview();
        }

        btnDownload.setOnClickListener(v -> saveFileToDownloads());
    }

    private void displayFilePreview() {
        if (fileType == null) {
            Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileType.startsWith("image/")) {
            // Display image
            imagePreview.setImageURI(fileUri);
            imagePreview.setVisibility(ImageView.VISIBLE);
            textPreview.setVisibility(TextView.GONE);
        } else if (fileType.equals("application/pdf")) {
            // Display first page of PDF
            displayPdfPreview();
        } else if (fileType.equals("text/plain")) {
            // Display text content
            displayTextFile();
        } else {
            Toast.makeText(this, "Preview not available for this file type", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPdfPreview() {
        try {
            ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(fileUri, "r");
            if (descriptor != null) {
                PdfRenderer renderer = new PdfRenderer(descriptor);
                PdfRenderer.Page page = renderer.openPage(0);

                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(ImageView.VISIBLE);
                textPreview.setVisibility(TextView.GONE);

                page.close();
                renderer.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void displayTextFile() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder textContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    textContent.append(line).append("\n");
                }
                textPreview.setText(textContent.toString());
                textPreview.setVisibility(TextView.VISIBLE);
                imagePreview.setVisibility(ImageView.GONE);
                inputStream.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error reading text file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveFileToDownloads() {
        try {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, "downloaded_file");
            values.put(MediaStore.Downloads.MIME_TYPE, fileType);

            Uri outputUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (outputUri != null) {
                try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
                     OutputStream outputStream = resolver.openOutputStream(outputUri)) {
                    if (inputStream != null && outputStream != null) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        Toast.makeText(this, "File Downloaded Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

