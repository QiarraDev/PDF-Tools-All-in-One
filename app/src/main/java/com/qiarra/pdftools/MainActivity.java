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
        binding.cardCompress
                .setOnClickListener(v -> Toast.makeText(this, "Compress tool coming soon", Toast.LENGTH_SHORT).show());
        binding.cardLock
                .setOnClickListener(v -> Toast.makeText(this, "Lock tool coming soon", Toast.LENGTH_SHORT).show());
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
        }
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
