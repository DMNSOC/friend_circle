package com.g.friendcirclemodule.utlis;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import androidx.core.content.FileProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class UtilityMethod{
    public static String formatDuration(long durationMs) {
        long seconds = durationMs / 1000;
        long min = seconds / 60;
        long sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    // px转dp
    public static int pxToDp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f); // 四舍五入
    }

    // dp转px（反向操作）
    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    // 保存裁剪后的资源到指定目录下
    public static void saveBitmapToDirectory(Bitmap bitmap, Context context, String name) {
        if (bitmap == null) return;

        // 获取应用的外部存储目录
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Use" + name + "Images");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 创建文件
        String fileName = "use" + name + "_image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException ignored) {}
    }
    public static int getUniqueId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("device_id.xml", 0);
        String uuid = prefs.getString("device_id", null);
        if (uuid != null) {
            return toShortHash(UUID.fromString(uuid));
        } else {
            uuid = UUID.randomUUID().toString();
            prefs.edit().putString("device_id", uuid).apply();
            return toShortHash(UUID.fromString(uuid));
        }
    }
    public static int toShortHash(UUID uuid) {
        return Math.abs(uuid.hashCode() % 1000000000);
    }

    public static byte[] readFileToBytes(Context ctx, Uri uri) throws IOException {
        InputStream is = ctx.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int n;
        while ((n = is.read(tmp)) > 0) buffer.write(tmp, 0, n);
        return buffer.toByteArray();
    }
    // 转换文件路径为ContentProvider URI
    public static Uri getContentUri(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }
        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        // 1. 通过OpenableColumns获取
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) {
                        result = cursor.getString(idx);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        // 2. content取不到时，直接解析Uri path
        if (result == null) {
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                if (cut != -1 && cut + 1 < path.length()) {
                    result = path.substring(cut + 1);
                } else {
                    result = path;
                }
            }
        }
        return result;
    }

}
