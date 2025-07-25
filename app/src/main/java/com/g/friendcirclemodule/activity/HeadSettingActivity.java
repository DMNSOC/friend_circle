package com.g.friendcirclemodule.activity;

import static com.g.friendcirclemodule.activity.MainActivity.uid;
import static com.g.friendcirclemodule.uc.ProtoApiClient.baseUrl;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.databinding.ActivityHeadSettingBinding;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.BaseModel;
import com.g.friendcirclemodule.uc.ProtoApiClient;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import com.google.protobuf.ByteString;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import user.UserOuterClass;

public class HeadSettingActivity extends BaseActivity<ActivityHeadSettingBinding, BaseModel> {

    int uId = uid;
    Uri htUri = Uri.parse("");

    @Override
    protected void initView() {
        super.initView();
        adjustCustomStatusBar(viewbinding.mainToolbar);
        List<DMEntryUseInfoBase> headInfoBaseList = FeedManager.getUseInfo(uId);
        if (!headInfoBaseList.isEmpty()) {
            DMEntryUseInfoBase dmEntryUseInfoBase = headInfoBaseList.get(0);
            if (dmEntryUseInfoBase.getFriendHead() != "" && dmEntryUseInfoBase.getFriendHead() != null) {
                Glide.with(getBaseContext())
                        .load(dmEntryUseInfoBase.getFriendHead())
                        .placeholder(R.mipmap.tx)
                        .override(300, 300)
                        .into(viewbinding.headTx); // Glide加载
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
                    .setUIProvider(new MyUIProvider())
                    .setSelectListener(selectedList -> {
                        startCropActivity(selectedList.get(0).uri);
                    })
                    .open();
        });

        viewbinding.headBtnSet.setOnClickListener(v -> {

            int useId = uId;

            UserOuterClass.BatchMediaUploadRequest.Builder batchReqBuilder = UserOuterClass.BatchMediaUploadRequest.newBuilder();
            String filename = UtilityMethod.getFileName(this, htUri); // 你可以自定义实现
            String mimeType = getContentResolver().getType(htUri);
            byte[] bytes = null; // 复用前面的通用方法
            try {
                bytes = UtilityMethod.readFileToBytes(this, htUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            UserOuterClass.MediaFile mediaFile = UserOuterClass.MediaFile.newBuilder()
                    .setFilename(filename)
                    .setMimeType(mimeType == null ? "" : mimeType)
                    .setData(ByteString.copyFrom(bytes))
                    .build();
            batchReqBuilder.addFiles(mediaFile);
            UserOuterClass.BatchMediaUploadRequest batchReq = batchReqBuilder.build();
            ProtoApiClient.achieveProto("/batch_upload_media", batchReq, UserOuterClass.BatchMediaUploadRequest.class, this, result -> {

                List<UserOuterClass.MediaFile> mediaList = result.getFilesList();
                UserOuterClass.Info info = UserOuterClass.Info.newBuilder()
                        .setUseId(useId)
                        .setFriendHead(baseUrl + mediaList.get(0).getData().toStringUtf8())
                        .build();
                ProtoApiClient.achieveProto("/update_info", info, UserOuterClass.Info.class, this, res -> {
                    DMEntryUseInfoBase dmEntryBase = new DMEntryUseInfoBase(res.getId(), res.getUseId(), res.getFriendName(), res.getFriendHead(), res.getFriendBg());
                    FeedManager.InsertItemToUserInfo(dmEntryBase);
                    finish();
                });

            });
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

    // 处理保存裁切后的图片
    private void handleUseHeadImage(Uri useHeadUri) {
        try {
            Bitmap useHeadBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(useHeadUri));
            htUri = useHeadUri;
            viewbinding.headTx.setImageBitmap(useHeadBitmap);
//            UtilityMethod.saveBitmapToDirectory(useHeadBitmap, this, "Head");

        } catch (IOException ignored) {}
    }
}