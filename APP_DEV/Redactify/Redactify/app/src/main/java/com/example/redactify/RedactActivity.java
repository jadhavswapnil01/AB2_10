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
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RedactActivity extends AppCompatActivity {

    private String fileUrl;
    private ImageView imagePreview;
    private TextView tvFileName;
    private Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact);

        tvFileName = findViewById(R.id.tvFileName);
        imagePreview = findViewById(R.id.imagePreview);
        btnDownload = findViewById(R.id.btnDownload);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fileUrl")) {
            fileUrl = intent.getStringExtra("fileUrl");
            tvFileName.setText("File Ready: " + fileUrl.substring(fileUrl.lastIndexOf("/") + 1));

            // Display PDF
            displayPdf(fileUrl);
        }

        btnDownload.setOnClickListener(v -> downloadFile(fileUrl));
    }

    private void displayPdf(String fileUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                File pdfFile = new File(getCacheDir(), "masked_file.pdf");

                FileOutputStream outputStream = new FileOutputStream(pdfFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                connection.disconnect();

                // Open the PDF preview
                runOnUiThread(() -> openPdf(pdfFile));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RedactActivity.this, "Failed to load masked PDF", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void openPdf(File file) {
        try {
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(descriptor);
            PdfRenderer.Page page = renderer.openPage(0);

            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imagePreview.setImageBitmap(bitmap);

            page.close();
            renderer.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void downloadFile(String fileUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "redacted_file.pdf");

                FileOutputStream outputStream = new FileOutputStream(downloadFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                connection.disconnect();

                runOnUiThread(() -> {
                    Toast.makeText(RedactActivity.this, "File Downloaded Successfully!", Toast.LENGTH_SHORT).show();
                    openDownloadedFile(downloadFile);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RedactActivity.this, "Failed to download file", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void openDownloadedFile(File file) {
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}


