package com.g.friendcirclemodule.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.adapter.DMEntryAdapter;
import com.g.friendcirclemodule.adapter.RecyclerViewPool;
import com.g.friendcirclemodule.databinding.ActivityMainBinding;
import com.g.friendcirclemodule.databinding.FriendEntryBinding;
import com.g.friendcirclemodule.databinding.MainTopBinding;
import com.g.friendcirclemodule.dialog.CommentDialog;
import com.g.friendcirclemodule.dp.CommentBase;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.EditDataManager;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.dp.AdapterVPBase;
import com.g.friendcirclemodule.model.MainActivityModel;
import com.g.friendcirclemodule.dialog.SettingDialog;
import com.g.friendcirclemodule.uc.WebSocketManager;
import com.g.friendcirclemodule.utlis.EnterImageUI;
import com.g.friendcirclemodule.uc.ProtoApiClient;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.MyUIProvider;
import com.g.mediaselector.PhotoLibrary;
import com.g.mediaselector.model.ResourceItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import com.g.friendcirclemodule.databinding.MoreDialogBinding;
import user.UserOuterClass;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainActivityModel> {
    public static Activity hostActivity;
    List<DMEntryBase> mData = new ArrayList<>();
    DMEntryAdapter adapter;
    int toolbarType = 1;
    int offsetY = 0;
    String useName = "";
    private Handler longPressHandler;
    private Runnable longPressRunnable;
    boolean isOpen = false;
    boolean isReceiverRegistered = false;
    public static int uid;
    public static boolean onLineState = false;
    public static WebSocketManager wsManager = new WebSocketManager();

    EnterImageUI eiu = new EnterImageUI();

    UserOuterClass.UserList userList;

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
        uid = UtilityMethod.getUniqueId(this);
        wsManager.connect(uid, ()->{
            onLineState = true;
            updateCommentData(null);
        });
        wsManager.userUpdatedEvent(()->{
            // 收到推送更新
            Intent intent = new Intent("ACTION_DIALOG_CLOSED");
            intent.putExtra("data_key", "更新数据");
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
        });
        wsManager.infoUpdatedEvent(()->{
            FeedManager.UpdateUseInfo();
            // 收到推送更新
            Intent intent = new Intent("ACTION_DIALOG_CLOSED");
            intent.putExtra("data_key", "更新数据");
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
        });
        wsManager.commentUpdatedEvent(() -> {
            updateCommentData(this::recyclerPosUpdate);
        });
        wsManager.discon(()->{
            onLineState = false;
        });

//        UserOuterClass.UserId userId = UserOuterClass.UserId.newBuilder()
//                .setId(uid)
//                .build();
//        ProtoApiClient.achieveProto("/get_users_list", userId, UserOuterClass.UserList.class, this, result -> {
//            Log.i("dddddd1", String.valueOf(result));
//        });

    }

    @Override
    protected void initView() {
        Log.i("99999999111111", String.valueOf(onLineState));
        super.initView();

        // 观察LiveData
        viewmodel.getMainRecyclerBase().observe(this, new Observer<>() {
            @Override
            public void onChanged(AdapterVPBase base) {
                if (base.pos == 0) {

                    MainTopBinding vb = (MainTopBinding)base.vb;
                    // 设置缓存的头像信息
                    List<DMEntryUseInfoBase> infoBaseList = FeedManager.getUseInfo(uid);
                    if (!infoBaseList.isEmpty()) {
                        DMEntryUseInfoBase dmEntryUseInfoBase = infoBaseList.get(0);
                        Log.i("Testttttttt", String.valueOf(infoBaseList));
                        if (!Objects.equals(dmEntryUseInfoBase.getFriendHead(), "") && dmEntryUseInfoBase.getFriendHead() != null) {
                            Glide.with(getBaseContext())
                                    .load(dmEntryUseInfoBase.getFriendHead())
                                    .placeholder(R.mipmap.tx)
                                    .override(300, 300)
                                    .into(vb.mainTopTx); // Glide加载
                        } else {
                            vb.mainTopTx.setImageResource(R.mipmap.tx);
                        }

                        if (!Objects.equals(dmEntryUseInfoBase.getFriendName(), "") && dmEntryUseInfoBase.getFriendName() != null) {
                            vb.mainTopName.setText(dmEntryUseInfoBase.getFriendName());
                            useName = dmEntryUseInfoBase.getFriendName();
                        } else {
                            vb.mainTopName.setText(R.string.user_name);
                            useName = getString(R.string.user_name);
                        }

                        if (!Objects.equals(dmEntryUseInfoBase.getFriendBg(), "") && dmEntryUseInfoBase.getFriendBg() != null) {

                            vb.mainTopBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(getBaseContext())
                                    .load(dmEntryUseInfoBase.getFriendBg())
                                    .placeholder(R.mipmap.bz1)
                                    .into(vb.mainTopBg); // Glide加载
                        } else {
                            vb.mainTopBg.setImageResource(R.mipmap.bz1);
                        }

                    } else {
                        vb.mainTopTx.setImageResource(R.mipmap.tx);
                        vb.mainTopName.setText(R.string.user_name);
                        useName = getString(R.string.user_name);
                        vb.mainTopBg.setImageResource(R.mipmap.bz1);
                    }

                    // 点击事件
                    vb.getRoot().setOnClickListener(v -> {
                        ViewGroup.LayoutParams params1 = vb.mainTop.getLayoutParams();
                        ViewGroup.LayoutParams params2 = vb.mainTopBg.getLayoutParams();
                        int dp = UtilityMethod.pxToDp(vb.getRoot().getContext(), params1.height);
                        int px1;
                        int px2;

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
                        if (!onLineState) {
                            Toast.makeText(getBaseContext(), R.string.tip_title_5, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent i = new Intent(hostActivity, BgReplaceMoreActivity.class);
                        hostActivity.startActivity(i);
                    });
                    vb.mainTopTx.setOnClickListener(view -> {
                        if (!onLineState) {
                            Toast.makeText(getBaseContext(), R.string.tip_title_5, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SettingDialog moreDialog = new SettingDialog(MainActivity.this);
                        moreDialog.show();
                    });
                } else {

                    // 朋友圈内容条目
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
                            Glide.with(getBaseContext())
                                    .load(dmEntryUseInfoBase.getFriendHead())
                                    .placeholder(R.mipmap.tx)
                                    .override(300, 300)
                                    .into(vb.friendEntryHead); // Glide加载
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
                    vb.friendEntryHead.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("USER_UID", dmEntryBase.getUseId());
                        Intent i = new Intent(hostActivity, VisitorActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    });


                    if (Objects.equals(dmEntryBase.getDecStr(), "")) {
                        vb.friendEntryDec.setVisibility(View.GONE);
                    } else {
                        vb.friendEntryDec.setVisibility(View.VISIBLE);
                        vb.friendEntryDec.setText(dmEntryBase.getDecStr());
                    }
                    vb.friendEntryTime.setText(vb.getRoot().getContext().getString(R.string.entry_time, String.valueOf(dmEntryBase.getTime())));

                    vb.catalogsList.setVisibility(View.GONE);
                    vb.interactLine.setVisibility(View.GONE);
                    vb.remarkList.setVisibility(View.GONE);

                    String likesId = dmEntryBase.getLikesId();
                    String[] likesArr = likesId.split(",");  // 按逗号分割
                    if (!Objects.equals(dmEntryBase.getLikesId(), "")) {
                        if (likesArr.length > 0) {
                            vb.catalogsList.setVisibility(View.VISIBLE);
                            String str = "";
                            for (String s : likesArr) {
                                List<DMEntryUseInfoBase> NIBL = FeedManager.getUseInfo(Integer.parseInt(s));
                                if (!NIBL.isEmpty()) {
                                    DMEntryUseInfoBase dmEntryUseInfoBase = NIBL.get(0);
                                    if (!Objects.equals(dmEntryUseInfoBase.getFriendName(), "") && dmEntryUseInfoBase.getFriendName() != null) {
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
                    Log.i("889889", Arrays.toString(likesArr) + "---" + uid);
                    moreDialog.moreDelete.setVisibility(dmEntryBase.getUseId() == uid ? View.VISIBLE : View.GONE);
                    if (Arrays.asList(likesArr).contains(String.valueOf(uid))) {
                        moreDialog.likeText.setVisibility(View.GONE);
                        moreDialog.likeRse.setVisibility(View.VISIBLE);
                    } else {
                        moreDialog.likeText.setVisibility(View.VISIBLE);
                        moreDialog.likeRse.setVisibility(View.GONE);
                    }

                    vb.friendEntryMore.setOnClickListener(v -> {
                        PopupWindow popup = new PopupWindow(moreDialog.getRoot(), 180, 400, true);
                        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popup.setTouchable(true);
                        popup.setOutsideTouchable(true);
                        popup.showAsDropDown(vb.friendEntryMore, -120, 0);

                        moreDialog.moreLike.setOnClickListener(v1 -> { // 点赞
                            StringBuilder likeStr = new StringBuilder();
                            if (Arrays.asList(likesArr).contains(String.valueOf(uid))) {
                                Log.i("889889", likeStr + "取消 === " + uid);
                                for (String s : likesArr) {
                                    if (!Objects.equals(s, String.valueOf(uid))) {
                                        if (likeStr.length() == 0){
                                            likeStr = new StringBuilder(s);
                                        } else {
                                            likeStr.append(",").append(s);
                                        }
                                    }
                                }
                            } else {
                                for (String s : likesArr) {
                                    if (likeStr.length() == 0){
                                        likeStr = new StringBuilder(s);
                                    } else {
                                        likeStr.append(",").append(s);
                                    }
                                }
                                if (likeStr.length() == 0){
                                    likeStr = new StringBuilder(String.valueOf(uid));
                                } else {
                                    likeStr.append(",").append(uid);
                                }
                                Log.i("889889", likeStr + "点赞 === " + uid);
                            }

                            if (onLineState) {
                                // 请求更新数据
                                UserOuterClass.UpdateUserRequest user = UserOuterClass.UpdateUserRequest.newBuilder()
                                        .setId(dmEntryBase.getId())
                                        .setLikesId(likeStr.toString())
                                        .build();
                                ProtoApiClient.achieveProto("/update_user", user, UserOuterClass.BoolResult.class, getParent(), result -> {
                                    Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                                    intent.putExtra("data_key", "更新数据");
                                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                                    popup.dismiss();
                                });
                            }

                        });
                        moreDialog.moreDelete.setOnClickListener(v1 -> { // 删除条目

                            if (onLineState) {
                                // 请求更新数据
                                UserOuterClass.DeleteUserRequest user = UserOuterClass.DeleteUserRequest.newBuilder()
                                        .setId(dmEntryBase.getId())
                                        .build();
                                ProtoApiClient.achieveProto("/delete_user", user, UserOuterClass.BoolResult.class, getParent(), result -> {
                                    Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                                    intent.putExtra("data_key", "更新数据");
                                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                                    popup.dismiss();
                                });
                            }

                        });
                        moreDialog.moreComment.setOnClickListener(v2 -> {
                            showCommentDialog(null, useName,dmEntryBase.getId());
                            popup.dismiss();
                        });
                    });
                    if (IGList.isEmpty()) {
                        vb.rvImages.setVisibility(View.GONE);
                    } else {
                        vb.rvImages.setVisibility(View.VISIBLE);
                        eiu.bindImageView(vb, IGList);
                    }

                    if (!dmEntryBase.getCommentList().isEmpty()) {
                        vb.interactLine.setVisibility(View.VISIBLE);
                        vb.remarkList.setVisibility(View.VISIBLE);
                        vb.commentContainer.removeAllViews();
                        Log.i("FATAL EXCEPTION",String.valueOf(dmEntryBase.getCommentList()));
                        for (CommentBase comment : dmEntryBase.getCommentList()) {
                            TextView commentView = new TextView(vb.getRoot().getContext());
                            String displayText = comment.getReplyTo() == null
                                    ? comment.getCommenter() + ": " + comment.getContent()
                                    : comment.getCommenter() + " 回复 " + comment.getReplyTo() + ": " + comment.getContent();
                            commentView.setText(displayText);
                            commentView.setPadding(8, 4, 8, 4);
                            commentView.setTextSize(14);
                            commentView.setOnClickListener(v -> showCommentDialog(comment, useName, dmEntryBase.getId())); // 点击评论回复
                            vb.commentContainer.addView(commentView);
                        }
                    }
                }
            }
        });

        // 设置状态栏高度
        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            viewbinding.mainToolbar.setPadding(0, statusBarHeight,0,0);
            return insets;
        });

        initInsets(viewbinding.main);
        hostActivity = this;
        viewbinding.mainBtnBack.setOnClickListener(v -> { finish();});

        longPressHandler = new Handler(Looper.getMainLooper());
        viewbinding.mainBtnCamera.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!onLineState) {
                            Toast.makeText(getBaseContext(), R.string.tip_title_5, Toast.LENGTH_SHORT).show();
                            break;
                        }
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
                        if (!onLineState) {
                            Toast.makeText(getBaseContext(), R.string.tip_title_5, Toast.LENGTH_SHORT).show();
                            break;
                        }
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

        // 列表信息请求
        UserOuterClass.Empty empty = UserOuterClass.Empty.newBuilder().build();
        ProtoApiClient.achieveProto("/list_users", empty, UserOuterClass.UserList.class, this, result -> {
            userList = result;
            updateListData();
            adapter = new DMEntryAdapter(mData, viewmodel);
            adapter.notifyDataSetChanged();
            // 设置优化
            viewbinding.mainRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            viewbinding.mainRecycler.setHasFixedSize(true);
            viewbinding.mainRecycler.setItemViewCacheSize(20); // 增大缓存池大小
            viewbinding.mainRecycler.setRecycledViewPool(RecyclerViewPool.getSharedPool());
            viewbinding.mainRecycler.setAdapter(adapter);

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

    @NonNull
    private static DMEntryBase getDmEntryBase(UserOuterClass.User user, List<CommentBase> list) {
        int id = user.getId();
        int useId = user.getUseId();
        String decStr = user.getDecStr();
        String friendImageId = user.getFriendImageId();
        String time = user.getTimeStr();
        String friendVideoId = user.getFriendVideoId();
        String friendVideoTime = user.getFriendVideoTime();
        String likesId = user.getLikesId();

        return new DMEntryBase(id, useId, decStr, friendImageId, time, friendVideoId, friendVideoTime, likesId, list);
    }

    public void updateListData() {
        // 2. 更新数据
        List<DMEntryBase> list = new ArrayList<>();
        List<CommentBase> l = new ArrayList<>();
        for (UserOuterClass.User user : userList.getUsersList()) {
            l = FeedManager.getCommentList(user.getId());
            DMEntryBase a = getDmEntryBase(user, l);
            list.add(a);
        }
        list.sort(Comparator.comparingInt(DMEntryBase::getId).reversed());
        mData.clear();
        mData.addAll(list);
    }

    @NonNull
    private static CommentBase getCommentBase(UserOuterClass.Comment user) {
        int id = user.getId();
        int groupId = user.getGroupId();
        String commenter = user.getCommenter();
        String replyTo = user.getReplyTo();
        String content = user.getContent();

        return new CommentBase(id, groupId, commenter, replyTo, content);
    }

    // 评论数据更新
    public void updateCommentData(Runnable c) {
        FeedManager.deleteAllComment();
        UserOuterClass.Empty commentEmpty = UserOuterClass.Empty.newBuilder().build();
        ProtoApiClient.achieveProto("/list_comment", commentEmpty, UserOuterClass.CommentList.class, this, result -> {

            for (UserOuterClass.Comment user : result.getCommentsList()) {
                Log.i("FATAL EXCEPTION",String.valueOf(getCommentBase(user)));
                FeedManager.InsertItemToComment(getCommentBase(user));
            }
            if (c != null) c.run();
        });

    }

    // 显示评论输入框
    private void showCommentDialog(CommentBase replyToComment, String useName,  int gid) {
        CommentDialog dialog = new CommentDialog(this, replyToComment, useName, gid);
        dialog.show();
    }

    public void onResume() {
        super.onResume();
        if (!onLineState) return;
        Log.i("gxgxgxgxgxg", "gxgxgx");
        if (adapter == null) return;
        eiu.dialogOnPlay();

        UserOuterClass.Empty empty = UserOuterClass.Empty.newBuilder().build();
        ProtoApiClient.achieveProto("/list_users", empty, UserOuterClass.UserList.class, this, result -> {
            userList = result;
            recyclerPosUpdate();
        });
    }

    public void recyclerPosUpdate() {
        // 1. 记录当前的滚动位置
        LinearLayoutManager layoutManager = (LinearLayoutManager) viewbinding.mainRecycler.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition);
        int offset = 0;
        if (firstVisibleView != null) {
            offset = firstVisibleView.getTop();
        }
        // 2. 更新数据
        updateListData();
        // 3. 通知数据变化
        adapter.notifyDataSetChanged();
        // 4. 恢复滚动位置
        layoutManager.scrollToPositionWithOffset(firstVisiblePosition, offset);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eiu.dialogOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wsManager.disconnect();
    }
}