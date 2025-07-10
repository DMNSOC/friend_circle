package com.g.friendcirclemodule.utlis;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

}
