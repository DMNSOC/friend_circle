package com.g.friendcirclemodule.utlis;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class SafeHandler extends Handler {
    private final WeakReference<Context> contextRef;
    Context context;

    public SafeHandler(Context context, Looper looper) {
        super(looper);
        this.contextRef = new WeakReference<>(context);
        this.context = context;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
