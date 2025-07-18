package com.g.friendcirclemodule.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.adapter.DMEntryAdapter;
import com.g.friendcirclemodule.adapter.RecyclerViewPool;
import com.g.friendcirclemodule.databinding.ActivityMainBinding;
import com.g.friendcirclemodule.databinding.FriendEntryBinding;
import com.g.friendcirclemodule.databinding.MainTopBinding;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.EditDataManager;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.dp.AdapterVPBase;
import com.g.friendcirclemodule.model.MainActivityModel;
import com.g.friendcirclemodule.dialog.SettingDialog;
import com.g.friendcirclemodule.utlis.EnterImageUI;
import com.g.friendcirclemodule.utlis.ProtoHttpClient;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import com.g.mediaselector.model.ResourceItem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.g.friendcirclemodule.databinding.MoreDialogBinding;

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

    EnterImageUI eiu = new EnterImageUI();

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
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {

        super.initView();
        // 请求接口
        new Thread(new Runnable() {
            @Override
            public void run() {
                ProtoHttpClient client = new ProtoHttpClient();
                try {
                    Log.i("111111", "列表： " + client.listUsers());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        // 观察LiveData
        viewmodel.getMainRecyclerBase().observe(this, new Observer<AdapterVPBase>() {
            @Override
            public void onChanged(AdapterVPBase base) {
                if (base.pos == 0) {

                    MainTopBinding vb = (MainTopBinding)base.vb;
                    // 设置缓存的头像信息
                    List<DMEntryUseInfoBase> infoBaseList = FeedManager.getUseInfo(1);
                    if (!infoBaseList.isEmpty()) {
                        DMEntryUseInfoBase dmEntryUseInfoBase = infoBaseList.get(0);
                        if (dmEntryUseInfoBase.getFriendHead() != "" && dmEntryUseInfoBase.getFriendHead() != null) {
                            Bitmap croppedBitmap = null;
                            try {
                                croppedBitmap = BitmapFactory.decodeStream(vb.getRoot().getContext().getContentResolver().openInputStream(Uri.parse(dmEntryUseInfoBase.getFriendHead())));
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            vb.mainTopTx.setImageBitmap(croppedBitmap);
                        } else {
                            vb.mainTopTx.setImageResource(R.mipmap.tx);
                        }

                        if (dmEntryUseInfoBase.getFriendName() != "" && dmEntryUseInfoBase.getFriendName() != null) {
                            vb.mainTopName.setText(dmEntryUseInfoBase.getFriendName());
                        } else {
                            vb.mainTopName.setText(R.string.user_name);
                        }

                        if (dmEntryUseInfoBase.getFriendBg() != "" && dmEntryUseInfoBase.getFriendBg() != null) {
                            Bitmap croppedBitmap = null;
                            try {
                                croppedBitmap = BitmapFactory.decodeStream(vb.getRoot().getContext().getContentResolver().openInputStream(Uri.parse(dmEntryUseInfoBase.getFriendBg())));
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }

                            vb.mainTopBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            vb.mainTopBg.setImageBitmap(croppedBitmap);
                        } else {
                            vb.mainTopBg.setImageResource(R.mipmap.bz1);
                        }

                    } else {
                        vb.mainTopTx.setImageResource(R.mipmap.tx);
                        vb.mainTopName.setText(R.string.user_name);
                        vb.mainTopBg.setImageResource(R.mipmap.bz1);
                    }

                    // 点击事件
                    vb.getRoot().setOnClickListener(v -> {
                        ViewGroup.LayoutParams params1 = vb.mainTop.getLayoutParams();
                        ViewGroup.LayoutParams params2 = vb.mainTopBg.getLayoutParams();
                        int dp = UtilityMethod.pxToDp(vb.getRoot().getContext(), params1.height);
                        int px1 = 0;
                        int px2 = 0;

                        if (dp == 600) {
                            vb.mainImages.setVisibility(View.GONE);
                            vb.mainHeadName.setVisibility(View.VISIBLE);
                            px1 = UtilityMethod.dpToPx(vb.getRoot().getContext(), 300);
                            px2 = UtilityMethod.dpToPx(vb.getRoot().getContext(), 240);
                        } else {
                            vb.mainHeadName.setVisibility(View.GONE);
                            vb.mainImages.setVisibility(View.VISIBLE);
                            px1 = UtilityMethod.dpToPx(vb.getRoot().getContext(), 600);
                            px2 = UtilityMethod.dpToPx(vb.getRoot().getContext(), 540);
                        }

                        ValueAnimator heightAnim1 = ValueAnimator.ofInt(vb.mainTop.getHeight(), px1);
                        heightAnim1.addUpdateListener(animation -> {
                            params1.height = (int) animation.getAnimatedValue();
                            vb.mainTop.setLayoutParams(params1);
                            vb.mainTop.requestLayout(); // 强制刷新布局
                        });
                        heightAnim1.setDuration(500).start();

                        ValueAnimator heightAnim2 = ValueAnimator.ofInt(vb.mainTopBg.getHeight(), px2);
                        heightAnim2.addUpdateListener(animation -> {
                            params2.height = (int) animation.getAnimatedValue();
                            vb.mainTopBg.setLayoutParams(params2);
                            vb.mainTopBg.requestLayout(); // 强制刷新布局
                        });
                        heightAnim2.setDuration(500).start();

                    });
                    vb.mainImages.setOnClickListener(v -> {
                        Intent i = new Intent(hostActivity, BgReplaceMoreActivity.class);
                        hostActivity.startActivity(i);
                    });
                    vb.mainTopTx.setOnClickListener(view -> {
                        SettingDialog moreDialog = new SettingDialog(MainActivity.this);
                        moreDialog.show();
                    });
                } else {
                    FriendEntryBinding vb = (FriendEntryBinding)base.vb;

                    ViewGroup.LayoutParams params = vb.dmeaMain.getLayoutParams();
                    params.width = sWidth;
                    vb.dmeaMain.setLayoutParams(params);

                    DMEntryBase dmEntryBase = (DMEntryBase) base.mData.get(base.pos - 1);
                    // 设置缓存的头像信息
                    List<DMEntryUseInfoBase> infoBaseList = FeedManager.getUseInfo(dmEntryBase.getUseId());
                    if (!infoBaseList.isEmpty()) {
                        DMEntryUseInfoBase dmEntryUseInfoBase = infoBaseList.get(0);

                        if (!Objects.equals(dmEntryUseInfoBase.getFriendHead(), "") && dmEntryUseInfoBase.getFriendHead() != null) {
                            Bitmap croppedBitmap = null;
                            try {
                                croppedBitmap = BitmapFactory.decodeStream(vb.getRoot().getContext().getContentResolver().openInputStream(Uri.parse(dmEntryUseInfoBase.getFriendHead())));
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            vb.friendEntryHead.setImageBitmap(croppedBitmap);
                        } else {
                            vb.friendEntryHead.setImageResource(R.mipmap.tx);
                        }

                        if (!Objects.equals(dmEntryUseInfoBase.getFriendName(), "") && dmEntryUseInfoBase.getFriendName() != null) {
                            vb.friendEntryName.setText(dmEntryUseInfoBase.getFriendName());
                        } else {
                            vb.friendEntryName.setText(R.string.user_name);
                        }

                    } else {
                        vb.friendEntryHead.setImageResource(R.mipmap.tx);
                        vb.friendEntryName.setText(R.string.user_name);
                    }

                    if (Objects.equals(dmEntryBase.getDecStr(), "")) {
                        vb.friendEntryDec.setVisibility(View.GONE);
                    } else {
                        vb.friendEntryDec.setVisibility(View.VISIBLE);
                        vb.friendEntryDec.setText(dmEntryBase.getDecStr());
                    }
                    vb.friendEntryTime.setText(vb.getRoot().getContext().getString(R.string.entry_time, String.valueOf(dmEntryBase.getTime())));
//                    vb.mainRvImages.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));

                    vb.catalogsList.setVisibility(View.GONE);

                    if (!Objects.equals(dmEntryBase.getLikesId(), "")) {
                        String likesId = dmEntryBase.getLikesId();
                        String[] likesArr = likesId.split(",");  // 按逗号分割
                        if (likesArr.length > 0) {
                            vb.catalogsList.setVisibility(View.VISIBLE);
                            String str = "";
                            for (String s : likesArr) {
                                List<DMEntryUseInfoBase> NIBL = FeedManager.getUseInfo(Integer.parseInt(s));
                                if (!NIBL.isEmpty()) {
                                    DMEntryUseInfoBase dmEntryUseInfoBase = NIBL.get(0);
                                    if (dmEntryUseInfoBase.getFriendName() != "" && dmEntryUseInfoBase.getFriendName() != null) {
                                        if (Objects.equals(str, "")) {
                                            str = dmEntryUseInfoBase.getFriendName();
                                        } else {
                                            str = str + "、" + dmEntryUseInfoBase.getFriendName();
                                        }
                                    } else {
                                        if (Objects.equals(str, "")) {
                                            str = MainActivity.this.getString(R.string.user_name);
                                        } else {
                                            str = str + "、" + MainActivity.this.getString(R.string.user_name);
                                        }
                                    }
                                } else {
                                    if (Objects.equals(str, "")) {
                                        str = MainActivity.this.getString(R.string.user_name);
                                    } else {
                                        str = str + "、" + MainActivity.this.getString(R.string.user_name);
                                    }
                                }
                            }
                            vb.catalogsText.setText(str);
                        }
                    }

                    List<ResourceItem> IGList = new ArrayList<>();
                    String imageStr = dmEntryBase.getFriendImageId();
                    String[] imageArr = imageStr.split(",");  // 按逗号分割
                    if (!imageStr.isEmpty() && imageArr.length >= 1) {
                        for (String s : imageArr) {
                            ResourceItem a = new ResourceItem(1, s, ResourceItem.TYPE_IMAGE,0, null);
                            IGList.add(a);
                        }
                    }
                    String videoStr = dmEntryBase.getFriendVideoId();
                    String[] videoArr = videoStr.split(",");  // 按逗号分割
                    String videoTimeStr = dmEntryBase.getFriendVideoTime();
                    String[] videoTimeArr = videoTimeStr.split(",");  // 按逗号分割
                    if (!videoStr.isEmpty() && videoArr.length >= 1) {
                        for (int i = 0; i < videoArr.length; i++) {
                            ResourceItem a = new ResourceItem(2, videoArr[i], ResourceItem.TYPE_VIDEO,Long.parseLong(videoTimeArr[i]), null);
                            IGList.add(a);
                        }
                    }

                    MoreDialogBinding moreDialog = MoreDialogBinding.inflate(LayoutInflater.from(vb.getRoot().getContext()), vb.getRoot(),false);
                    if (dmEntryBase.getLikeState() == 1) {
                        moreDialog.likeText.setVisibility(View.GONE);
                        moreDialog.likeRse.setVisibility(View.VISIBLE);
                    } else {
                        moreDialog.likeText.setVisibility(View.VISIBLE);
                        moreDialog.likeRse.setVisibility(View.GONE);
                    }

                    vb.friendEntryMore.setOnClickListener(v -> {
                        PopupWindow popup = new PopupWindow(moreDialog.getRoot(), 180, 250, true);
                        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popup.setTouchable(true);
                        popup.setOutsideTouchable(true);
                        popup.showAsDropDown(vb.friendEntryMore, -120, 0);

                        moreDialog.moreLike.setOnClickListener(v1 -> { // 点赞
                            int likeState = dmEntryBase.getLikeState();
                            String likesId = dmEntryBase.getLikesId();
                            String[] likesArr = likesId.split(",");  // 按逗号分割
                            StringBuilder likeStr = new StringBuilder();
                            if (likeState == 1) {
                                for (String s : likesArr) {
                                    if (!Objects.equals(s, "1")) {
                                        if (likeStr.length() == 0){
                                            likeStr = new StringBuilder(s);
                                        } else {
                                            likeStr.append(",").append(s);
                                        }
                                    }
                                }
                                likeState = 0;
                            } else {
                                for (String s : likesArr) {
                                    if (Objects.equals(s, "1")) {
                                        if (likeStr.length() == 0){
                                            likeStr = new StringBuilder(s);
                                        } else {
                                            likeStr.append(",").append(s);
                                        }
                                    }
                                }
                                likeStr.append(1);
                                likeState = 1;
                            }

                            DMEntryBase aeb = new DMEntryBase(dmEntryBase.getId(), dmEntryBase.getUseId(), dmEntryBase.getDecStr(), dmEntryBase.getFriendImageId(), dmEntryBase.getTime(), dmEntryBase.getFriendVideoId(), dmEntryBase.getFriendVideoTime(), likeState, likeStr.toString());
                            FeedManager.UpdateItemToAccounttb(aeb);

                            Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                            intent.putExtra("data_key", "更新数据");
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                            popup.dismiss();

                        });
                        moreDialog.moreDelete.setOnClickListener(v1 -> { // 删除条目
                            int click_id = dmEntryBase.getId();
                            FeedManager.deleteItemFromAccounttbById(click_id);
                            Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                            intent.putExtra("data_key", "更新数据");
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                            popup.dismiss();
                        });
                    });
                    if (IGList.isEmpty()) {
                        vb.rvImages.setVisibility(View.GONE);
                    } else {
                        vb.rvImages.setVisibility(View.VISIBLE);
                        eiu.bindImageView(vb, IGList);
                    }
                }
            }
        });

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
        adapter = new DMEntryAdapter(mData, viewmodel);
        // 设置优化
        viewbinding.mainRecycler.setLayoutManager(new LinearLayoutManager(this));
        viewbinding.mainRecycler.setHasFixedSize(true);
        viewbinding.mainRecycler.setItemViewCacheSize(20); // 增大缓存池大小

        viewbinding.mainRecycler.setAdapter(adapter);
        viewbinding.mainRecycler.setRecycledViewPool(RecyclerViewPool.getSharedPool());

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
        eiu.dialogOnPlay();
        List<DMEntryBase> list;
        list = FeedManager.getTypeList();
        mData.clear();
        mData.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        eiu.dialogOnPause();
    }
}