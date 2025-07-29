package com.g.friendcirclemodule.dialog;

import static com.g.friendcirclemodule.activity.MainActivity.uid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.databinding.SetNameDialogBinding;
import com.g.friendcirclemodule.dp.CommentBase;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.BaseModel;
import com.g.friendcirclemodule.uc.ProtoApiClient;
import com.g.friendcirclemodule.utlis.SafeHandler;

import user.UserOuterClass;

public class CommentDialog extends BaseDialog<SetNameDialogBinding, BaseModel> {
    private final Activity context;
    String useName = "";
    int groupId;
    CommentBase replyToComment;

    public CommentDialog(@NonNull Activity context, CommentBase replyToComment, String useName, int groupId) {
        super(context);
        this.useName = useName;
        this.context = context;
        this.groupId = groupId;
        this.replyToComment = replyToComment;
    }

    @Override
    protected void initView() {
        super.initView();

        String hint = this.replyToComment == null ? getContext().getString(R.string.comment_dec_tips) : getContext().getString(R.string.btn_comment) + this.replyToComment.getCommenter() + " :";
        String ReplyTo = this.replyToComment == null? "" : this.replyToComment.getCommenter();
        viewbinding.setNameTv.setText(R.string.comment_title);
        viewbinding.setNameEt.setHint(hint);
        viewbinding.setNameBtnEnsure.setText(R.string.btn_comment);
        viewbinding.setNameBtnCancel.setOnClickListener(v -> {
            cancel();
        });
        viewbinding.setNameBtnEnsure.setOnClickListener(v -> {

            String commentText = viewbinding.setNameEt.getText().toString().trim();
            if (!TextUtils.isEmpty(commentText)) {
                UserOuterClass.Comment empty = UserOuterClass.Comment.newBuilder()
                        .setGroupId(this.groupId)
                        .setCommenter(this.useName)
                        .setReplyTo(ReplyTo)
                        .setContent(commentText)
                        .build();
                ProtoApiClient.achieveProto("/create_comment", empty, UserOuterClass.Comment.class, this.context, result -> {
                    cancel();
                });
//                        UserOuterClass.Empty empty = UserOuterClass.Empty.newBuilder().build();
//                        ProtoApiClient.achieveProto("/list_users", empty, UserOuterClass.UserList.class, this, result -> {});
//                        String replyTo = replyToComment == null ? null : replyToComment.getCommenter();
//                        new CommentBase("当前用户", replyTo, commentText)
////                        post.getComments().add(new Comment("当前用户", replyTo, commentText));
////                        postAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(viewbinding.getRoot().getContext(), "评论内容不能为空！", Toast.LENGTH_SHORT).show();
            }
        });
        Handler mHandler = new SafeHandler(context, Looper.getMainLooper());
        mHandler.sendEmptyMessageDelayed(1,300);
    }



    public void onDismiss() {
        Intent intent = new Intent("ACTION_DIALOG_CLOSED");
        intent.putExtra("data_key", "更新数据");
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }




}