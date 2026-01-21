package com.qiarra.pdftools;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.qiarra.pdftools.databinding.ActivityMainBinding;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int REQUEST_CODE_PICK_IMAGES = 1001;
    private static final int REQUEST_CODE_PICK_PDFS = 1002;
    private static final int REQUEST_CODE_PICK_PDF_COMPRESS = 1003;
    private static final int REQUEST_CODE_PICK_PDF_LOCK = 1004;
    private static final int PERMISSION_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PdfHelper.init(this);
        setupClickListeners();
        checkPermissions();
    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String[] permissions = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
            };
            requestPermissions(permissions, PERMISSION_CODE);
        }
    }

    private void setupClickListeners() {
        binding.cardScan.setOnClickListener(v -> pickImages());
        binding.cardMerge.setOnClickListener(v -> pickPdfs());
        binding.cardCompress.setOnClickListener(v -> pickPdfForCompress());
        binding.cardLock.setOnClickListener(v -> pickPdfForLock());
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE_PICK_IMAGES);
    }

    private void pickPdfs() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select PDF Files"), REQUEST_CODE_PICK_PDFS);
    }

    private void pickPdfForCompress() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF to Compress"), REQUEST_CODE_PICK_PDF_COMPRESS);
    }

    private void pickPdfForLock() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF to Lock"), REQUEST_CODE_PICK_PDF_LOCK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            List<String> paths = new ArrayList<>();
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    paths.add(FileUtils.getPath(this, data.getClipData().getItemAt(i).getUri()));
                }
            } else if (data.getData() != null) {
                paths.add(FileUtils.getPath(this, data.getData()));
            }

            if (!paths.isEmpty()) {
                processImagesToPdf(paths);
            }
        } else if (requestCode == REQUEST_CODE_PICK_PDFS && resultCode == RESULT_OK && data != null) {
            List<String> paths = new ArrayList<>();
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    paths.add(FileUtils.getPath(this, data.getClipData().getItemAt(i).getUri()));
                }
            } else if (data.getData() != null) {
                paths.add(FileUtils.getPath(this, data.getData()));
            }

            if (!paths.isEmpty()) {
                processMergePdfs(paths);
            }
        } else if (requestCode == REQUEST_CODE_PICK_PDF_COMPRESS && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String path = FileUtils.getPath(this, uri);
                processCompressPdf(path);
            }
        } else if (requestCode == REQUEST_CODE_PICK_PDF_LOCK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String path = FileUtils.getPath(this, uri);
                showPasswordDialog(path);
            }
        }
    }

    private void showPasswordDialog(String inputPath) {
        android.widget.EditText et = new android.widget.EditText(this);
        et.setHint("Enter Password");
        et.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Lock PDF")
                .setView(et)
                .setPositiveButton("Lock", (dialog, which) -> {
                    String password = et.getText().toString();
                    if (!password.isEmpty()) {
                        processLockPdf(inputPath, password);
                    } else {
                        Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processLockPdf(String inputPath, String password) {
        final android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Locking PDF...");
        pd.setCancelable(false);
        pd.show();

        new Thread(() -> {
            try {
                String outputFolder = getExternalFilesDir(null).getAbsolutePath() + "/MyPDFs";
                File folder = new File(outputFolder);
                if (!folder.exists())
                    folder.mkdirs();

                String outputPath = outputFolder + "/locked_" + System.currentTimeMillis() + ".pdf";
                PdfHelper.lockPdf(inputPath, outputPath, password);

                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "PDF Locked: " + outputPath, Toast.LENGTH_LONG).show();
                    openPdf(outputPath);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void processCompressPdf(String inputPath) {
        final android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Compressing PDF...");
        pd.setCancelable(false);
        pd.show();

        new Thread(() -> {
            try {
                String outputFolder = getExternalFilesDir(null).getAbsolutePath() + "/MyPDFs";
                File folder = new File(outputFolder);
                if (!folder.exists())
                    folder.mkdirs();

                String outputPath = outputFolder + "/compressed_" + System.currentTimeMillis() + ".pdf";
                PdfHelper.compressPdf(inputPath, outputPath);

                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "PDF Compressed: " + outputPath, Toast.LENGTH_LONG).show();
                    openPdf(outputPath);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void processMergePdfs(List<String> paths) {
        final android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Merging PDFs...");
        pd.setCancelable(false);
        pd.show();

        new Thread(() -> {
            try {
                String outputFolder = getExternalFilesDir(null).getAbsolutePath() + "/MyPDFs";
                File folder = new File(outputFolder);
                if (!folder.exists())
                    folder.mkdirs();

                String outputPath = outputFolder + "/merged_" + System.currentTimeMillis() + ".pdf";
                PdfHelper.mergePdfs(paths, outputPath);

                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "PDFs Merged: " + outputPath, Toast.LENGTH_LONG).show();
                    openPdf(outputPath);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void processImagesToPdf(List<String> paths) {
        final android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Generating PDF...");
        pd.setCancelable(false);
        pd.show();

        new Thread(() -> {
            try {
                String outputFolder = getExternalFilesDir(null).getAbsolutePath() + "/MyPDFs";
                File folder = new File(outputFolder);
                if (!folder.exists())
                    folder.mkdirs();

                String outputPath = outputFolder + "/result_" + System.currentTimeMillis() + ".pdf";
                PdfHelper.imagesToPdf(paths, outputPath);

                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "PDF saved to: " + outputPath, Toast.LENGTH_LONG).show();
                    openPdf(outputPath);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void openPdf(String path) {
        File file = new File(path);
        Uri uri = androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }
}
