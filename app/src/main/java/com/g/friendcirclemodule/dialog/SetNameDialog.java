package com.g.friendcirclemodule.dialog;

import static com.g.friendcirclemodule.activity.MainActivity.uid;
import static com.g.friendcirclemodule.utlis.ProtoApiClient.baseUrl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.g.friendcirclemodule.databinding.SetNameDialogBinding;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.BaseModel;
import com.g.friendcirclemodule.utlis.ProtoApiClient;
import com.g.friendcirclemodule.utlis.SafeHandler;
import user.UserOuterClass;

public class SetNameDialog extends BaseDialog<SetNameDialogBinding, BaseModel> {
    private final Context context;
    int uId = 1;
    public SetNameDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void initView() {
        super.initView();
        viewbinding.setNameBtnCancel.setOnClickListener(v -> {
            cancel();
        });
        viewbinding.setNameBtnEnsure.setOnClickListener(v -> {
            int useId = uid;
            String friendName = String.valueOf(viewbinding.setNameEt.getText());
            UserOuterClass.Info info = UserOuterClass.Info.newBuilder()
                    .setUseId(useId)
                    .setFriendName(friendName)
                    .build();
            ProtoApiClient.achieveProto("/update_info", info, UserOuterClass.Info.class, null, res -> {
                DMEntryUseInfoBase dmEntryBase = new DMEntryUseInfoBase(res.getId(), res.getUseId(), res.getFriendName(), res.getFriendHead(), res.getFriendBg());
                FeedManager.InsertItemToUserInfo(dmEntryBase);
                cancel();
            });
        });
        Handler mHandler = new SafeHandler(context, Looper.getMainLooper());
        mHandler.sendEmptyMessageDelayed(1,300);
    }

    @Override
    public void setDialogSize() {
        super.setDialogSize();
        Handler mHandler = new SafeHandler(context, Looper.getMainLooper());
//        mHandler.sendEmptyMessageDelayed(1,300);
//        handleKeyboardVisibility();
    }


    public void onDismiss() {
        Intent intent = new Intent("ACTION_DIALOG_CLOSED");
        intent.putExtra("data_key", "更新数据");
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }



    private void handleKeyboardVisibility() {

        // 监听布局变化（键盘弹出或收起时会触发）
        viewbinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                viewbinding.getRoot().getWindowVisibleDisplayFrame(r);

                // 获取屏幕高度
                int screenHeight = viewbinding.getRoot().getRootView().getHeight();

                // 计算键盘高度
                int keyboardHeight = screenHeight - r.bottom;
                // 判断键盘是否弹出
                if (screenHeight == 452 && r.bottom <= 2200) { // 键盘弹出（阈值可以根据设备调整）
                    viewbinding.getRoot().setPadding(0, 0, 0, 1076); // 上移 Dialog
                } else if (r.bottom >= 2200 && screenHeight < 452) {
                    viewbinding.getRoot().setPadding(0, 0, 0, 0); // 恢复原位

                }
            }
        });
    }

}