package com.g.friendcirclemodule.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.g.friendcirclemodule.databinding.ActivityBgReplaceMoreBinding;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.BaseModel;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class BgReplaceMoreActivity extends BaseActivity<ActivityBgReplaceMoreBinding, BaseModel> {

    int uId = 1;
    @Override
    protected void initView() {
        super.initView();
        adjustCustomStatusBar(viewbinding.mainToolbar);
        viewbinding.mainBtnBack.setOnClickListener(v -> {
            finish();
        });
        viewbinding.btnReplace.setOnClickListener(v -> {
            new PhotoLibrary.Builder(this)
                    .setMode(PhotoLibrary.MODE_IMAGE)
                    .setMultiSelect(false)
                    .setUIProvider(new MyUIProvider())
                    .setSelectListener(selectedList -> {
                            startCropActivity(selectedList.get(0).uri);
                    })
                    .open();


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
        File destinationFile = new File(getCacheDir(), "useCover_image.jpg");
        Uri destinationUri = Uri.fromFile(destinationFile);
        // 配置裁切工具
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        // 设置裁切的宽高比例为 1:1 (正方形)
        uCrop.withAspectRatio(1, 2);
        // 设置裁切后的图片最大宽高
        uCrop.withMaxResultSize(690, 1490);
        // 启动裁切工具
        uCrop.start(this);
    }

    // 处理保存裁切后的图片
    private void handleUseHeadImage(Uri useHeadUri) {
        try {
            Bitmap useHeadBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(useHeadUri));;
            List<DMEntryUseInfoBase> coverInfoBaseList = FeedManager.getUseInfo(uId);
            long id = 1;
            int useId = 1;
            String friendName = "";
            String friendHead = "";
            String friendBg = String.valueOf(useHeadUri);
            if (!coverInfoBaseList.isEmpty()) {
                DMEntryUseInfoBase dmEntryUseInfoBase = coverInfoBaseList.get(0);
                id = dmEntryUseInfoBase.getId();
                friendName = dmEntryUseInfoBase.getFriendName();
                useId = dmEntryUseInfoBase.getUseId();
                friendHead = dmEntryUseInfoBase.getFriendHead();
            }
            DMEntryUseInfoBase dmEntryBase = new DMEntryUseInfoBase(id, useId, friendName, friendHead, friendBg);
            FeedManager.InsertItemToUserInfo(dmEntryBase);
            UtilityMethod.saveBitmapToDirectory(useHeadBitmap, this, "Cover");
            finish();
        } catch (IOException ignored) {}
    }
}