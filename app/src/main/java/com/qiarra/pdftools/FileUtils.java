package com.qiarra.pdftools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static String getPath(Context context, Uri uri) {
        String path = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        }

        if (path == null) {
            // Fallback for newer Android versions or different providers
            path = copyFileToCache(context, uri);
        }
        return path;
    }

    private static String copyFileToCache(Context context, Uri uri) {
        try {
            File cacheDir = new File(context.getCacheDir(), "temp_files");
            if (!cacheDir.exists())
                cacheDir.mkdirs();

            String fileName = "temp_" + System.currentTimeMillis();
            File tempFile = new File(cacheDir, fileName);

            InputStream is = context.getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
