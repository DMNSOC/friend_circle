package com.g.friendcirclemodule.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.g.friendcirclemodule.adapter.ImageGridAdapter;
import com.g.friendcirclemodule.databinding.ActivityContentEditingBinding;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.EditDataManager;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.ContentEditingActivityModel;
import com.g.friendcirclemodule.utlis.DragToDeleteCallback;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import com.g.mediaselector.model.ResourceItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ContentEditingActivity extends BaseActivity<ActivityContentEditingBinding, ContentEditingActivityModel> {
    ImageGridAdapter adapter;
    List<ResourceItem> list = new ArrayList<>();
    int type = 1;
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

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size); // 包含导航栏和状态栏
        int itemNum = 4;

        itemNum = (int) Math.floor((double) size.x / 400);

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

            DMEntryBase dmEntryBase = new DMEntryBase(id, useId, dec, imagePath, time, videoPath, friendVideoTime);
            FeedManager.InsertItemToAccounttb(dmEntryBase);
            finish();
        });

        if (type == 2) {
            viewbinding.rvImages.setLayoutManager(new GridLayoutManager(this, itemNum));
            adapter = new ImageGridAdapter(list);
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (type == 2) {
            // 释放播放器资源
            adapter.stopCurrentPlayer();
        }
    }

}