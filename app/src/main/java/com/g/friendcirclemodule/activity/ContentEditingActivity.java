package com.g.friendcirclemodule.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.adapter.ImageGridAdapter;
import com.g.friendcirclemodule.databinding.ActivityContentEditingBinding;
import com.g.friendcirclemodule.databinding.CeRibItemBinding;
import com.g.friendcirclemodule.dialog.PDPlayerBase;
import com.g.friendcirclemodule.dp.AdapterVPBase;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.EditDataManager;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.ContentEditingActivityModel;
import com.g.friendcirclemodule.utlis.DragToDeleteCallback;
import com.g.friendcirclemodule.utlis.ProtoHttpClient;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import com.g.mediaselector.model.ResourceItem;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ContentEditingActivity extends BaseActivity<ActivityContentEditingBinding, ContentEditingActivityModel> {
    ImageGridAdapter adapter;
    ExoPlayer player;
    List<ResourceItem> list = new ArrayList<>();
    List<PDPlayerBase> playerList = new ArrayList<>();
    int type = 1;
    int itemNum = 3;
    int selectNum = 6;
    @Override
    protected void initData() {
        adjustCustomStatusBar(viewbinding.mainToolbar);
        initInsets(viewbinding.main);
        Bundle receivedBundle = getIntent().getExtras();
        if (receivedBundle != null) {
            type = receivedBundle.getInt("TYPE");
        }
        if (type == 2) {
            list.clear();
            list.addAll(EditDataManager.getList());
        }
    }

    @Override
    protected void initView() {
        super.initView();
        viewmodel.getImageGridBase().observe(this, new Observer<AdapterVPBase>() {
            @Override
            public void onChanged(AdapterVPBase base) {
                CeRibItemBinding vb = (CeRibItemBinding) base.vb;

                ViewGroup.LayoutParams params = vb.ceRib.getLayoutParams();

                int width = (sWidth - UtilityMethod.dpToPx(ContentEditingActivity.this, 90)) / itemNum;
                Log.i("tessssst", String.valueOf(width));
                int dp = UtilityMethod.pxToDp(ContentEditingActivity.this, width) - 2;
                params.width = UtilityMethod.dpToPx(getBaseContext(), dp);
                params.height = UtilityMethod.dpToPx(getBaseContext(), dp);

                vb.ceRib.setLayoutParams(params);

                vb.playerView.setVisibility(View.GONE);
                vb.ivImage.setVisibility(View.VISIBLE);
                vb.videoTime.setVisibility(View.GONE);
                if (base.pos == (base.mData.size())) {
                    vb.ivImage.setImageResource(R.mipmap.add);
                } else {
                    ResourceItem item = (ResourceItem) base.mData.get(base.pos);
                    if (item.type == ResourceItem.TYPE_IMAGE) {
                        Glide.with(vb.getRoot()).load(item.path).into(vb.ivImage); // Glide加载
                    } else {
                        vb.playerView.setVisibility(View.VISIBLE);
                        vb.videoTime.setVisibility(View.VISIBLE);
                        vb.ivImage.setVisibility(View.GONE);
                        // 1. 初始化播放器
                        player = new ExoPlayer.Builder(vb.getRoot().getContext()).build();
                        vb.playerView.setPlayer(player);
                        // 2. 设置媒体源（支持本地/网络URI）
                        MediaItem mediaItem = MediaItem.fromUri(item.path);
                        player.setMediaItem(mediaItem);
                        player.prepare();
                        playerList.add(new PDPlayerBase(player, base.pos));
                    }
                }
            }
        });



        viewbinding.ceBtnCancel.setOnClickListener(v -> {finish();});
        viewbinding.ceBtnPublish.setOnClickListener(v -> {
            Date data = new Date();
            long id = data.getTime();
            int useId = 1;
            String dec = viewbinding.ceDescribe.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm");
            String time = sdf.format(data);
            String imagePath = "";
            String videoPath = "";
            String friendVideoTime = "";
            for (ResourceItem resourceItem : list) {
                if (resourceItem.type == ResourceItem.TYPE_IMAGE) {
                    if(Objects.equals(imagePath, "")) {
                        imagePath = resourceItem.path;
                    } else  {
                        imagePath = imagePath + "," + resourceItem.path;
                    }
                } else {
                    if(Objects.equals(videoPath, "")) {
                        friendVideoTime = String.valueOf(resourceItem.duration);
                        videoPath = resourceItem.path;
                    } else  {
                        friendVideoTime = friendVideoTime + "," + resourceItem.duration;
                        videoPath = videoPath + "," + resourceItem.path;
                    }
                }
            }

//            DMEntryBase dmEntryBase = new DMEntryBase((int)id, useId, dec, imagePath, time, videoPath, friendVideoTime, 0, "");
//            FeedManager.InsertItemToAccounttb(dmEntryBase);

            // 请求接口
            String finalImagePath = imagePath;
            String finalVideoPath = videoPath;
            String finalFriendVideoTime = friendVideoTime;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ProtoHttpClient client = new ProtoHttpClient();
                    try {
                        client.createUser(useId, dec, finalImagePath, time, finalVideoPath, finalFriendVideoTime, 0, "");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            finish();
        });

        if (type == 2) {
            viewbinding.rvImages.setLayoutManager(new GridLayoutManager(this, itemNum));
            adapter = new ImageGridAdapter(list, viewmodel);
            viewbinding.rvImages.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //点击事件
            adapter.setOnItemClickListener((hv) -> {
                onItemClickListener(hv);
            });
            // 设置 ItemTouchHelper
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DragToDeleteCallback(adapter, viewbinding.deleteArea));
            itemTouchHelper.attachToRecyclerView(viewbinding.rvImages);
        }
    }

    private void onItemClickListener(RecyclerView.ViewHolder hv) {

        if (hv.getBindingAdapterPosition() == (list.size())) {
            new PhotoLibrary.Builder(this)
                    .setMode(PhotoLibrary.MODE_ALL)
                    .setMultiSelect(true)
                    .setSelectNum((selectNum - list.size()))
                    .setUIProvider(new MyUIProvider())
                    .setSelectListener(selectedList -> {
                        EditDataManager.addList(selectedList);
                        onResume();
                    })
                    .open();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type == 2) {
            list.clear();
            list.addAll(EditDataManager.getList());
            adapter.notifyDataSetChanged();
        }
    }

    protected void onPause() {
        super.onPause();
        if (type == 2) {
            if (playerList != null && !playerList.isEmpty()) {
                for (PDPlayerBase pdPlayerBase : playerList) {
                    pdPlayerBase.exoPlayer.stop();
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (type == 2) {
            // 释放播放器资源
            if (playerList != null && !playerList.isEmpty()) {
                for (PDPlayerBase pdPlayerBase : playerList) {
                    pdPlayerBase.exoPlayer.stop();
                    pdPlayerBase.exoPlayer.release();
                    pdPlayerBase.exoPlayer = null;
                }
            }
        }
    }
}