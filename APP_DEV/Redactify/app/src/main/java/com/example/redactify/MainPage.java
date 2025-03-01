package com.example.redactify;

/*import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private Uri pdfUri; // Stores the selected PDF URI
    private Button btnSelectPDF, btnPreviewPDF;
    private TextView txtFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        btnSelectPDF = findViewById(R.id.btnSelectPDF);
        btnPreviewPDF = findViewById(R.id.btnPreviewPDF);
        txtFileName = findViewById(R.id.txtFileName);

        // Select PDF button
        btnSelectPDF.setOnClickListener(view -> selectPDF());

        // Preview PDF button
        btnPreviewPDF.setOnClickListener(view -> previewPDF());
    }

    // Method to select a PDF file
    private void selectPDF() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    // Handle the result of PDF selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            if (pdfUri != null) {
                txtFileName.setText("Selected: " + DocumentsContract.getDocumentId(pdfUri));
                btnPreviewPDF.setVisibility(View.VISIBLE); // Show preview button
            }
        }
    }

    // Method to preview the selected PDF file
    private void previewPDF() {
        if (pdfUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No PDF viewer installed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No PDF selected!", Toast.LENGTH_SHORT).show();
        }
    }
}*/

/*import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private Uri selectedPdfUri;
    private CheckBox checkBoxAgree;
    private Button btnRedact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Button btnSelectPdf = findViewById(R.id.btnSelectPdf);
        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        btnRedact = findViewById(R.id.btnRedact);

        btnSelectPdf.setOnClickListener(v -> selectPdf());
        btnRedact.setOnClickListener(v -> proceedToRedact());
    }

    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    @Override
    /*protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
            Toast.makeText(this, "PDF Selected", Toast.LENGTH_SHORT).show();
        }
    }*/

    /*protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedPdfUri = data.getData();

            if (selectedPdfUri != null) {
                Toast.makeText(this, "Selected PDF URI: " + selectedPdfUri.toString(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to get PDF URI", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void proceedToRedact() {
        if (!checkBoxAgree.isChecked()) {
            Toast.makeText(this, "You need to check the checkbox to proceed", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPdfUri == null) {
            Toast.makeText(this, "Please select a PDF first", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainPage.this, RedactActivity.class);
        intent.putExtra("pdfUri", selectedPdfUri.toString());
        startActivity(intent);
    }
}*/

/*import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private Uri selectedPdfUri;
    private CheckBox checkBoxAgree;
    private Button btnRedact, btnPreviewPdf;
    private TextView txtPdfName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Button btnSelectPdf = findViewById(R.id.btnSelectPdf);
        btnPreviewPdf = findViewById(R.id.btnPreviewPdf);
        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        btnRedact = findViewById(R.id.btnRedact);
        txtPdfName = findViewById(R.id.txtPdfName);

        btnSelectPdf.setOnClickListener(v -> selectPdf());
        btnPreviewPdf.setOnClickListener(v -> previewPdf());
        btnRedact.setOnClickListener(v -> proceedToRedact());

        checkBoxAgree.setOnCheckedChangeListener((buttonView, isChecked) -> checkEnableRedact());
    }

    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedPdfUri = data.getData();

            if (selectedPdfUri != null) {
                String pdfName = selectedPdfUri.getLastPathSegment();
                txtPdfName.setText("Selected PDF: " + pdfName);
                btnPreviewPdf.setVisibility(Button.VISIBLE);
                checkEnableRedact();
            } else {
                Toast.makeText(this, "Failed to get PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void previewPdf() {
        if (selectedPdfUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedPdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No PDF selected to preview", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEnableRedact() {
        btnRedact.setEnabled(selectedPdfUri != null && checkBoxAgree.isChecked());
    }

    private void proceedToRedact() {
        Intent intent = new Intent(MainPage.this, RedactActivity.class);
        intent.putExtra("pdfUri", selectedPdfUri.toString());
        startActivity(intent);
    }
}*/

/*import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri selectedFileUri;
    private CheckBox checkBoxAgree;
    private Button btnRedact, btnPreview;
    private TextView txtFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Button btnSelectFile = findViewById(R.id.btnSelectFile);
        btnPreview = findViewById(R.id.btnPreview);
        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        btnRedact = findViewById(R.id.btnRedact);
        txtFileName = findViewById(R.id.txtFileName);

        btnSelectFile.setOnClickListener(v -> selectFile());
        btnPreview.setOnClickListener(v -> previewFile());
        btnRedact.setOnClickListener(v -> proceedToRedact());

        checkBoxAgree.setOnCheckedChangeListener((buttonView, isChecked) -> checkEnableRedact());
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*//*"); // Allow all file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"application/pdf", "image/*", "text/plain", "application/msword"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();

            if (selectedFileUri != null) {
                String fileName = selectedFileUri.getLastPathSegment();
                txtFileName.setText("Selected File: " + fileName);
                btnPreview.setVisibility(Button.VISIBLE);
                checkEnableRedact();
            } else {
                Toast.makeText(this, "Failed to get file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void previewFile() {
        if (selectedFileUri != null) {
            String fileType = getContentResolver().getType(selectedFileUri);

            if (fileType != null) {
                if (fileType.startsWith("image/")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(selectedFileUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else if (fileType.equals("application/pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(selectedFileUri, "application/pdf");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Preview not supported for this file type", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No file selected to preview", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEnableRedact() {
        btnRedact.setEnabled(selectedFileUri != null && checkBoxAgree.isChecked());
    }

    private void proceedToRedact() {
        Intent intent = new Intent(MainPage.this, RedactActivity.class);
        intent.putExtra("fileUri", selectedFileUri.toString());
        startActivity(intent);
    }
}*/

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainPage extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri selectedFileUri;
    private CheckBox checkBoxAgree;
    private Button btnRedact;
    private TextView txtFileName, textPreview;
    private ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Button btnSelectFile = findViewById(R.id.btnSelectFile);
        btnRedact = findViewById(R.id.btnRedact);
        txtFileName = findViewById(R.id.txtFileName);
        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        imagePreview = findViewById(R.id.imagePreview);
        textPreview = findViewById(R.id.textPreview);

        btnSelectFile.setOnClickListener(v -> selectFile());
        btnRedact.setOnClickListener(v -> proceedToRedact());

        checkBoxAgree.setOnCheckedChangeListener((buttonView, isChecked) -> checkEnableRedact());
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"application/pdf", "image/*", "text/plain"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                txtFileName.setText("Selected File: " + selectedFileUri.getLastPathSegment());
                previewFile();
                checkEnableRedact();
            } else {
                Toast.makeText(this, "Failed to get file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void previewFile() {
        String fileType = getContentResolver().getType(selectedFileUri);
        if (fileType == null) {
            Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileType.startsWith("image/")) {
            imagePreview.setImageURI(selectedFileUri);
            imagePreview.setVisibility(ImageView.VISIBLE);
            textPreview.setVisibility(TextView.GONE);
        } else if (fileType.equals("application/pdf")) {
            displayPdfPreview();
        } else if (fileType.equals("text/plain")) {
            displayTextFile();
        } else {
            Toast.makeText(this, "Preview not available for this file type", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPdfPreview() {
        try {
            ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(selectedFileUri, "r");
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
            InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
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

    private void checkEnableRedact() {
        btnRedact.setEnabled(selectedFileUri != null && checkBoxAgree.isChecked());
    }

    private void proceedToRedact() {
        Intent intent = new Intent(MainPage.this, RedactActivity.class);
        intent.putExtra("fileUri", selectedFileUri.toString());
        startActivity(intent);
    }
}



