package com.g.friendcirclemodule.utlis;

import static com.g.friendcirclemodule.activity.MainActivity.hostActivity;
import static com.g.friendcirclemodule.uc.ProtoApiClient.baseUrl;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.databinding.FriendEntryBinding;
import com.g.friendcirclemodule.dialog.PreviewDialog;
import com.g.friendcirclemodule.interface_method.EnterImageUIProvider;
import com.g.mediaselector.model.ResourceItem;
import java.util.Arrays;
import java.util.List;

public class EnterImageUI implements EnterImageUIProvider {

    PreviewDialog dialog;
    @Override
    public void bindImageView(FriendEntryBinding binding, List<ResourceItem> list) {
        if (list.size() > 1) {

            binding.ceRelative0.setVisibility(View.GONE);
//
            binding.reImagesGrid.setVisibility(View.VISIBLE);
            List<View> imageViews = Arrays.asList(
                    binding.reImagesOne, binding.reImagesTwo, binding.reImagesThree,
                    binding.reImagesFour, binding.reImagesFive, binding.reImagesSix,
                    binding.reImagesSeven, binding.reImagesEight, binding.reImagesNine
            );
            List<View> videoTimeViews = Arrays.asList(
                    binding.videoTimeOne, binding.videoTimeTwo, binding.videoTimeThree,
                    binding.videoTimeFour, binding.videoTimeFive, binding.videoTimeSix,
                    binding.videoTimeSeven, binding.videoTimeEight, binding.videoTimeNine
            );
            List<View> views = Arrays.asList(
                    binding.ceRelative1, binding.ceRelative2, binding.ceRelative3,
                    binding.ceRelative4, binding.ceRelative5, binding.ceRelative6,
                    binding.ceRelative7, binding.ceRelative8, binding.ceRelative9
            );

            for (View view : videoTimeViews) {
                if (view instanceof TextView) {
                    view.setVisibility(View.GONE);
                }
            }

            for (View view : views) {
                if (view instanceof RelativeLayout) {
                    view.setVisibility(View.GONE);
                }
            }

            for (int i = 0; i < list.size(); i++) {
                views.get(i).setVisibility(View.VISIBLE);

                Glide.with(binding.getRoot())
                        .load(baseUrl + list.get(i).path)
                        .placeholder(R.mipmap.question_mark)
                        .override(300, 300)
                        .into((ImageView) imageViews.get(i)); // Glide加载


                if (list.get(i).type == ResourceItem.TYPE_VIDEO) {
                    videoTimeViews.get(i).setVisibility(View.VISIBLE);
                    ((TextView) videoTimeViews.get(i)).setText(UtilityMethod.formatDuration(list.get(i).duration));
                }

                // 点击事件
                int finalI = i;
                views.get(i).setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("PATH", list.get(finalI).path);
                    bundle.putInt("TYPE",  list.get(finalI).type);
                    Context context = hostActivity;
                    dialog = new PreviewDialog(context, (List<ResourceItem>) list, finalI);
                    dialog.show();
                    dialog.setDialogSize();
                });
            }
        } else {
            binding.ceRelative0.setVisibility(View.VISIBLE);
            binding.reImagesGrid.setVisibility(View.GONE);

            ViewGroup.LayoutParams params = binding.reImagesZero.getLayoutParams();

//            if (list.get(0).type == ResourceItem.TYPE_VIDEO) {
//                File videoFile = new File(list.get(0).path);
//                if(videoFile.exists()) {
//                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                    retriever.setDataSource(list.get(0).path); // 支持文件路径或Uri
//                    String widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//                    String heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
//                    if (widthStr != null && heightStr != null) {
//                        int width = Integer.parseInt(widthStr);
//                        int height = Integer.parseInt(heightStr);
//                        params.width = UtilityMethod.pxToDp(binding.getRoot().getContext(), width * 2);
//                        params.height = UtilityMethod.pxToDp(binding.getRoot().getContext(), height * 2);
//                    }
//                    try {
//                        retriever.release();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//            } else {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true; // 仅解码尺寸
//                BitmapFactory.decodeFile(list.get(0).path, options);
//                int width = options.outWidth;
//                int height = options.outHeight;
//                params.width = UtilityMethod.pxToDp(binding.getRoot().getContext(), width);
//                params.height = UtilityMethod.pxToDp(binding.getRoot().getContext(), height);
//            }

            binding.videoTimeZero.setVisibility(View.GONE);

            if (list.get(0).type == ResourceItem.TYPE_VIDEO) {
                binding.videoTimeZero.setVisibility(View.VISIBLE);
                binding.videoTimeZero.setText(UtilityMethod.formatDuration(list.get(0).duration));
            }

            int defaultWidth =  UtilityMethod.dpToPx(binding.getRoot().getContext(), 200);
            if (params.width < defaultWidth) {
                float x = (float) defaultWidth / params.width;
                params.width = (int) (params.width * x);
                params.height = (int) (params.height * x);
            }

            binding.reImagesZero.setLayoutParams(params);

            Log.i("99999999999", baseUrl + list.get(0).path);

            Glide.with(binding.getRoot())
                    .load(baseUrl + list.get(0).path)
                    .placeholder(R.mipmap.question_mark)
                    .override(300, 300)
                    .into(binding.reImagesZero); // Glide加载

            // 点击事件
            binding.reImagesZero.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("PATH", baseUrl + list.get(0).path);
                bundle.putInt("TYPE",  list.get(0).type);
                Context context = hostActivity;
                dialog = new PreviewDialog(context, (List<ResourceItem>) list, 0);
                dialog.show();
                dialog.setDialogSize();
            });
        }
    }

    public void dialogOnPlay() {
        if (dialog != null) {
            dialog.onPlay();
        }
    }
    public void dialogOnPause() {
        if (dialog != null) {
            dialog.onPause();
        }
    }
}
