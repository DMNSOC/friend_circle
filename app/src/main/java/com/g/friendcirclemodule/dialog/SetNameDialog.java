package com.g.friendcirclemodule.dialog;

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
import com.g.friendcirclemodule.utlis.SafeHandler;

import java.util.List;

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
            List<DMEntryUseInfoBase> coverInfoBaseList = FeedManager.getUseInfo(uId);
            DMEntryUseInfoBase dmEntryUseInfoBase = coverInfoBaseList.get(0);
            String name = String.valueOf(viewbinding.setNameEt.getText());
            DMEntryUseInfoBase dmEntryBase = new DMEntryUseInfoBase(dmEntryUseInfoBase.getId(), dmEntryUseInfoBase.getUseId(), name, dmEntryUseInfoBase.getFriendHead(), dmEntryUseInfoBase.getFriendBg());
            FeedManager.InsertItemToUserInfo(dmEntryBase);
            cancel();
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