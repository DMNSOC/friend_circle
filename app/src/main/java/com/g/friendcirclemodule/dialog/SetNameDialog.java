package com.g.friendcirclemodule.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.g.friendcirclemodule.databinding.SetNameDialogBinding;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.model.BaseModel;

public class SetNameDialog extends BaseDialog<SetNameDialogBinding, BaseModel> {
    int uId = 1;
    public SetNameDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        super.initView();
        viewbinding.setNameBtnCancel.setOnClickListener(v -> {
            cancel();
        });
        viewbinding.setNameBtnEnsure.setOnClickListener(v -> {
            String name = String.valueOf(viewbinding.setNameEt.getText());
            Log.i("dddddd", name);
            DMEntryUseInfoBase dmEntryBase = new DMEntryUseInfoBase(2, uId,name, "");
            FeedManager.InsertItemToUserInfo(dmEntryBase);
            cancel();
        });
    }

    @Override
    public void setDialogSize() {
        super.setDialogSize();
        handler.sendEmptyMessageDelayed(1,100);
    }


    public void onDismiss() {
        Intent intent = new Intent("ACTION_DIALOG_CLOSED");
        intent.putExtra("data_key", "更新数据");
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };
}