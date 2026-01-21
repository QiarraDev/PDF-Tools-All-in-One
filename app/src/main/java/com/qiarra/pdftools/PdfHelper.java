package com.qiarra.pdftools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfHelper {

    public static void init(Context context) {
        PDFBoxResourceLoader.init(context);
    }

    /**
     * Convert images to a single PDF
     */
    public static void imagesToPdf(List<String> imagePaths, String outputPath) throws IOException {
        PDDocument document = new PDDocument();
        try {
            for (String path : imagePaths) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                PDPage page = new PDPage(new PDRectangle(bitmap.getWidth(), bitmap.getHeight()));
                document.addPage(page);

                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bitmap);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.drawImage(pdImage, 0, 0);
                contentStream.close();
                bitmap.recycle();
            }
            document.save(outputPath);
        } finally {
            document.close();
        }
    }

    /**
     * Merge multiple PDFs into one
     */
    public static void mergePdfs(List<String> pdfPaths, String outputPath) throws IOException {
        PDDocument resultDoc = new PDDocument();
        try {
            for (String path : pdfPaths) {
                PDDocument sourceDoc = PDDocument.load(new File(path));
                for (PDPage page : sourceDoc.getPages()) {
                    resultDoc.addPage(page);
                }
                sourceDoc.close();
            }
            resultDoc.save(outputPath);
        } finally {
            resultDoc.close();
        }
    }

    /**
     * Lock PDF with password
     */
    public static void lockPdf(String inputPath, String outputPath, String password) throws IOException {
        PDDocument document = PDDocument.load(new File(inputPath));
        try {
            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
            spp.setEncryptionKeyLength(128);
            spp.setPermissions(ap);
            document.protect(spp);
            document.save(outputPath);
        } finally {
            document.close();
        }
    }

    /**
     * "Compress" PDF by re-saving it.
     * Note: Real compression usually involves downsampling images, which is more
     * complex in PdfBox.
     * This is a simplified version.
     */
    public static void compressPdf(String inputPath, String outputPath) throws IOException {
        PDDocument document = PDDocument.load(new File(inputPath));
        try {
            // PdfBox's save incremental or just basic save sometimes reduces size if
            // original was unoptimized
            document.save(outputPath);
        } finally {
            document.close();
        }
    }
}
