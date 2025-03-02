package com.example.redactify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainPage extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri selectedFileUri;
    private CheckBox checkBoxAgree;
    private Button btnRedact, btnSelectFile;
    private TextView txtFileName, textPreview;
    private ImageView imagePreview;
    private ProgressDialog progressDialog;  // Loading dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnRedact = findViewById(R.id.btnRedact);
        txtFileName = findViewById(R.id.txtFileName);
        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        imagePreview = findViewById(R.id.imagePreview);
        textPreview = findViewById(R.id.textPreview);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);

        btnSelectFile.setOnClickListener(v -> selectFile());
        btnRedact.setOnClickListener(v -> uploadFile(selectedFileUri));

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
                txtFileName.setText("Selected File: " + getFileName(selectedFileUri));
                previewFile();
                checkEnableRedact();
            } else {
                Toast.makeText(this, "Failed to get file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "Unknown";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileName = cursor.getString(nameIndex);
            cursor.close();
        }
        return fileName;
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

    private void uploadFile(Uri fileUri) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)  // Increase connection timeout
                .readTimeout(5, TimeUnit.MINUTES)     // Increase read timeout
                .writeTimeout(5, TimeUnit.MINUTES)    // Increase write timeout
                .build();

        if (fileUri == null) {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        String uploadUrl = "https://9a1a-2402-8100-39e3-aa45-35f5-707e-acda-5bb.ngrok-free.app/upload.php";
        File file = new File(getFilesDir(), getFileName(fileUri));

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), fileBytes))
                    .build();

            Request request = new Request.Builder()
                    .url(uploadUrl)
                    .post(requestBody)
                    .build();

//            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainPage.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }

                @Override

                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response.body().string());
                                String fileUrl = jsonResponse.getString("fileUrl");

                                Intent intent = new Intent(MainPage.this, RedactActivity.class);
                                intent.putExtra("fileUrl", fileUrl);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MainPage.this, "Failed to parse server response", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Toast.makeText(MainPage.this, "Server error: " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error processing file", Toast.LENGTH_SHORT).show();
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
}
