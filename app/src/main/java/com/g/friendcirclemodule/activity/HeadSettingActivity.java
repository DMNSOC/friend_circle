package com.g.friendcirclemodule.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.databinding.ActivityHeadSettingBinding;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.BaseModel;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HeadSettingActivity extends BaseActivity<ActivityHeadSettingBinding, BaseModel> {

    int uId = 1;
    Uri htUri = Uri.parse("");

    @Override
    protected void initView() {
        super.initView();
        adjustCustomStatusBar(viewbinding.mainToolbar);
        List<DMEntryUseInfoBase> headInfoBaseList = FeedManager.getUseInfo(1, uId);
        if (!headInfoBaseList.isEmpty()) {
            DMEntryUseInfoBase dmEntryUseInfoBase = headInfoBaseList.get(0);
            Log.i("dddddd", String.valueOf(dmEntryUseInfoBase.getFriendHead()));
            if (dmEntryUseInfoBase.getFriendHead() != "" && dmEntryUseInfoBase.getFriendHead() != null) {
                Bitmap useHeadBitmap = null;
                try {
                    useHeadBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(dmEntryUseInfoBase.getFriendHead())));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                viewbinding.headTx.setImageBitmap(useHeadBitmap);
            } else {
                viewbinding.headTx.setImageResource(R.mipmap.tx);
            }
        } else {
            viewbinding.headTx.setImageResource(R.mipmap.tx);
        }

        viewbinding.mainBtnBack.setOnClickListener(v -> {finish();});
        viewbinding.mainBtnFolder.setOnClickListener(v -> {
            new PhotoLibrary.Builder(this)
                    .setMode(PhotoLibrary.MODE_IMAGE)
                    .setMultiSelect(false)
                    .setTopBarName("头像")
                    .setUIProvider(new MyUIProvider())
                    .setSelectListener(selectedList -> {
                        startCropActivity(selectedList.get(0).uri);
                    })
                    .open();
        });

        viewbinding.headBtnSet.setOnClickListener(v -> {
            DMEntryUseInfoBase dmEntryBase = new DMEntryUseInfoBase(1, uId,"", String.valueOf(htUri));
            FeedManager.InsertItemToUserInfo(dmEntryBase);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            // 获取裁切后的图片
            Uri useHeadUri = UCrop.getOutput(data);
            if (useHeadUri != null) {
                handleUseHeadImage(useHeadUri);
            }
        }
    }

    // 启动裁切工具
    private void startCropActivity(Uri sourceUri) {
        // 设置裁切后的输出路径
        File destinationFile = new File(getCacheDir(), "useHead_image.jpg");
        Uri destinationUri = Uri.fromFile(destinationFile);
        // 配置裁切工具
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        // 设置裁切的宽高比例为 1:1 (正方形)
        uCrop.withAspectRatio(1, 1);
        // 设置裁切后的图片最大宽高
        uCrop.withMaxResultSize(500, 500);
        // 启动裁切工具
        uCrop.start(this);
    }

    // 处理保存裁切后的图片并指定目录
    private void handleUseHeadImage(Uri useHeadUri) {
        try {
            Bitmap useHeadBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(useHeadUri));
            htUri = useHeadUri;
            viewbinding.headTx.setImageBitmap(useHeadBitmap);
            saveBitmapToDirectory(useHeadBitmap);

        } catch (IOException ignored) {}
    }
    private void saveBitmapToDirectory(Bitmap bitmap) {
        if (bitmap == null) return;

        // 获取应用的外部存储目录
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "UseHeadImages");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 创建文件
        String fileName = "useHead_image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException ignored) {}
    }
}