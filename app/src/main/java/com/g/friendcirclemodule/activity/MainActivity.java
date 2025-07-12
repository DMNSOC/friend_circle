package com.g.friendcirclemodule.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.adapter.DMEntryAdapter;
import com.g.friendcirclemodule.databinding.ActivityMainBinding;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.EditDataManager;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.MainActivityModel;
import com.g.friendcirclemodule.dialog.SettingDialog;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainActivityModel> {
    public static Activity hostActivity;
    List<DMEntryBase> mData = new ArrayList<>();
    DMEntryAdapter adapter;
    int toolbarType = 1;
    int offsetY = 0;
    private Handler longPressHandler;
    private Runnable longPressRunnable;
    boolean isOpen = true;

    boolean isReceiverRegistered = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this) // 注册广播接收器
                    .registerReceiver(receiver, new IntentFilter("ACTION_DIALOG_CLOSED"));
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(receiver);
            isReceiverRegistered = false;
        }
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_DIALOG_CLOSED".equals(intent.getAction())) {
                onResume();
            }
        }
    };

    @Override
    protected void initView() {
        getStatusBarHeight();
        viewbinding.mainToolbar.setPadding(0,getStatusBarHeight(),0,0);
        initInsets(viewbinding.main);
        hostActivity = this;
        viewbinding.mainBtnBack.setOnClickListener(v -> { finish();});

        longPressHandler = new Handler(Looper.getMainLooper());
        viewbinding.mainBtnCamera.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 启动长按
                        longPressRunnable = () -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("TYPE", 1);
                            Intent i = new Intent(hostActivity, ContentEditingActivity.class);
                            i.putExtras(bundle);
                            startActivity(i);
                            isOpen = true;
                        };
                        longPressHandler.postDelayed(longPressRunnable, 500); // 500ms 长按时间
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // 移除长按检测
                        longPressHandler.removeCallbacks(longPressRunnable);
                        if (!isOpen) {
                            new PhotoLibrary.Builder(hostActivity)
                                    .setMode(PhotoLibrary.MODE_ALL)
                                    .setMultiSelect(true)
                                    .setSelectNum(6)
                                    .setUIProvider(new MyUIProvider())
                                    .setSelectListener(selectedList -> {
                                        EditDataManager.setList(selectedList);
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("TYPE", 2);
                                        Intent i = new Intent(hostActivity, ContentEditingActivity.class);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    })
                                    .open();
                        }
                        isOpen = false;
                        break;
                }
                return true;
            }
        });

        List<DMEntryBase> list;
        list = FeedManager.getTypeList();
        mData.clear();
        mData.addAll(list);
        adapter = new DMEntryAdapter(mData);
        // 设置优化
        viewbinding.mainRecycler.setLayoutManager(new LinearLayoutManager(this));
        viewbinding.mainRecycler.setHasFixedSize(true);
        viewbinding.mainRecycler.setItemViewCacheSize(20); // 增大缓存池大小

        viewbinding.mainRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new DMEntryAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(DMEntryAdapter.HeaderViewHolder hvh) {
                SettingDialog moreDialog = new SettingDialog(hostActivity);
                moreDialog.show();
            }
        });
        viewbinding.mainRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() { // 监听方法
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                offsetY = offsetY + dy;
                int maxScroll = 600;
                int alpha = 0;
                if (offsetY >= maxScroll) {
                    if (toolbarType == 1) {
                        viewbinding.mainBtnBack.setImageResource(R.mipmap.arrow_back_black);
                        viewbinding.mainBtnCamera.setImageResource(R.mipmap.photo_camera_black);
                        viewbinding.mainTitle.setText(getString(R.string.app_name));
                        toolbarType = 2;
                    }
                    alpha = (int)((offsetY - maxScroll) / (float)maxScroll * 255);
                } else {
                    if (toolbarType == 2) {
                        viewbinding.mainBtnBack.setImageResource(R.mipmap.arrow_back_white);
                        viewbinding.mainBtnCamera.setImageResource(R.mipmap.photo_camera_white);
                        viewbinding.mainTitle.setText("");
                        toolbarType = 1;
                    }
                }
                alpha = Math.min(alpha, 255);
                viewbinding.mainTitle.setTextColor(Color.argb(alpha, 0, 0, 0));
                viewbinding.mainToolbar.setBackgroundColor(Color.argb(alpha, 125, 125, 125));
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (adapter.dialog != null) {
            adapter.dialog.onPlay();
        }
        List<DMEntryBase> list;
        list = FeedManager.getTypeList();
        mData.clear();
        mData.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter.dialog != null) {
            adapter.dialog.onPause();
        }
    }
}