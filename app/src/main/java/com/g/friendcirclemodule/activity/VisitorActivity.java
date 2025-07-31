package com.g.friendcirclemodule.activity;

import static com.g.friendcirclemodule.activity.MainActivity.onLineState;
import static com.g.friendcirclemodule.activity.MainActivity.uid;
import static com.g.friendcirclemodule.activity.MainActivity.wsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
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
import com.g.friendcirclemodule.databinding.MoreDialogBinding;
import com.g.friendcirclemodule.dialog.CommentDialog;
import com.g.friendcirclemodule.dp.AdapterVPBase;
import com.g.friendcirclemodule.dp.CommentBase;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.MainActivityModel;
import com.g.friendcirclemodule.uc.ProtoApiClient;
import com.g.friendcirclemodule.utlis.EnterImageUI;
import com.g.mediaselector.model.ResourceItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import user.UserOuterClass;

public class VisitorActivity extends BaseActivity<ActivityMainBinding, MainActivityModel> {
    int userUid;
    UserOuterClass.UserList userList;
    List<DMEntryBase> mData = new ArrayList<>();
    DMEntryAdapter adapter;
    int toolbarType = 1;
    int offsetY = 0;
    String useName = "";
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
        Bundle receivedBundle = getIntent().getExtras();
        if (receivedBundle != null) {
            userUid = receivedBundle.getInt("USER_UID");
        }
        wsManager.commentUpdatedEvent(() -> {
            updateCommentData(this::recyclerPosUpdate);
        });
        wsManager.discon(()->{
            onLineState = false;
        });
    }

    @Override
    protected void initView() {
        super.initView();

        // 观察LiveData
        viewmodel.getMainRecyclerBase().observe(this, new Observer<>() {
            @Override
            public void onChanged(AdapterVPBase base) {
                if (base.pos == 0) {

                    MainTopBinding vb = (MainTopBinding)base.vb;
                    // 设置缓存的头像信息
                    List<DMEntryUseInfoBase> infoBaseList = FeedManager.getUseInfo(userUid);
                    if (!infoBaseList.isEmpty()) {
                        DMEntryUseInfoBase dmEntryUseInfoBase = infoBaseList.get(0);
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
                                            str = VisitorActivity.this.getString(R.string.user_name);
                                        } else {
                                            str = str + "、" + VisitorActivity.this.getString(R.string.user_name);
                                        }
                                    }
                                } else {
                                    if (Objects.equals(str, "")) {
                                        str = VisitorActivity.this.getString(R.string.user_name);
                                    } else {
                                        str = str + "、" + VisitorActivity.this.getString(R.string.user_name);
                                    }
                                }
                            }
                            vb.catalogsText.setText(str);
                        }
                    }

                    List<ResourceItem> IGList = new ArrayList<>();
                    String imageStr = dmEntryBase.getFriendImageId();
                    String[] imageArr = imageStr.split(",");
                    if (!imageStr.isEmpty() && imageArr.length >= 1) {
                        for (String s : imageArr) {
                            ResourceItem a = new ResourceItem(1, s, ResourceItem.TYPE_IMAGE,0, null);
                            IGList.add(a);
                        }
                    }
                    String videoStr = dmEntryBase.getFriendVideoId();
                    String[] videoArr = videoStr.split(",");
                    String videoTimeStr = dmEntryBase.getFriendVideoTime();
                    String[] videoTimeArr = videoTimeStr.split(",");
                    if (!videoStr.isEmpty() && videoArr.length >= 1) {
                        for (int i = 0; i < videoArr.length; i++) {
                            ResourceItem a = new ResourceItem(2, videoArr[i], ResourceItem.TYPE_VIDEO,Long.parseLong(videoTimeArr[i]), null);
                            IGList.add(a);
                        }
                    }

                    MoreDialogBinding moreDialog = MoreDialogBinding.inflate(LayoutInflater.from(vb.getRoot().getContext()), vb.getRoot(),false);
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

                        moreDialog.moreLike.setOnClickListener(v1 -> {
                            StringBuilder likeStr = new StringBuilder();
                            if (Arrays.asList(likesArr).contains(String.valueOf(uid))) {
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
                            }

                            if (onLineState) {
                                UserOuterClass.UpdateUserRequest user = UserOuterClass.UpdateUserRequest.newBuilder()
                                        .setId(dmEntryBase.getId())
                                        .setLikesId(likeStr.toString())
                                        .build();
                                ProtoApiClient.achieveProto("/update_user", user, UserOuterClass.BoolResult.class, getParent(), result -> {
                                    Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                                    intent.putExtra("data_key", "更新数据");
                                    LocalBroadcastManager.getInstance(VisitorActivity.this).sendBroadcast(intent);
                                    popup.dismiss();
                                });
                            }

                        });
                        moreDialog.moreDelete.setOnClickListener(v1 -> {

                            if (onLineState) {
                                UserOuterClass.DeleteUserRequest user = UserOuterClass.DeleteUserRequest.newBuilder()
                                        .setId(dmEntryBase.getId())
                                        .build();
                                ProtoApiClient.achieveProto("/delete_user", user, UserOuterClass.BoolResult.class, getParent(), result -> {
                                    Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                                    intent.putExtra("data_key", "更新数据");
                                    LocalBroadcastManager.getInstance(VisitorActivity.this).sendBroadcast(intent);
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

        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            viewbinding.mainToolbar.setPadding(0, statusBarHeight,0,0);
            return insets;
        });

        initInsets(viewbinding.main);
        viewbinding.mainBtnBack.setOnClickListener(v -> { finish();});
        viewbinding.mainBtnCamera.setVisibility(View.GONE);

        UserOuterClass.UserId userId = UserOuterClass.UserId.newBuilder()
                .setId(userUid)
                .build();
        ProtoApiClient.achieveProto("/get_users_list", userId, UserOuterClass.UserList.class, this, result -> {
            userList = result;
            updateListData();
            adapter = new DMEntryAdapter(mData, viewmodel);
            adapter.notifyDataSetChanged();
            viewbinding.mainRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            viewbinding.mainRecycler.setHasFixedSize(true);
            viewbinding.mainRecycler.setItemViewCacheSize(20);
            viewbinding.mainRecycler.setRecycledViewPool(RecyclerViewPool.getSharedPool());
            viewbinding.mainRecycler.setAdapter(adapter);

        });

        viewbinding.mainRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        viewbinding.mainTitle.setText(getString(R.string.visitor_title, useName));
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

    public void updateCommentData(Runnable c) {
        FeedManager.deleteAllComment();
        UserOuterClass.Empty commentEmpty = UserOuterClass.Empty.newBuilder().build();
        ProtoApiClient.achieveProto("/list_comment", commentEmpty, UserOuterClass.CommentList.class, this, result -> {

            for (UserOuterClass.Comment user : result.getCommentsList()) {
                FeedManager.InsertItemToComment(getCommentBase(user));
            }
            if (c != null) c.run();
        });

    }

    private void showCommentDialog(CommentBase replyToComment, String useName,  int gid) {
        CommentDialog dialog = new CommentDialog(this, replyToComment, useName, gid);
        dialog.show();
    }

    public void onResume() {
        super.onResume();
        if (!onLineState) return;
        if (adapter == null) return;
        eiu.dialogOnPlay();

        UserOuterClass.UserId userId = UserOuterClass.UserId.newBuilder()
                .setId(userUid)
                .build();
        ProtoApiClient.achieveProto("/get_users_list", userId, UserOuterClass.UserList.class, this, result -> {
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
    }
}