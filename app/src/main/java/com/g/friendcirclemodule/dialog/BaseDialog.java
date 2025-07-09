package com.g.friendcirclemodule.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;
import com.g.friendcirclemodule.interface_method.BaseBindingInterface;
import java.lang.reflect.Field;

public class BaseDialog<VB extends ViewBinding,VM extends ViewModel> extends Dialog implements BaseBindingInterface<VB, VM> {
    public BaseDialog(@NonNull Context context) {
        super(context);
    }
    final  String[] bv = {"viewbinding", "viewmodel"};
    public VB viewbinding;
    public VM viewmodel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewBinding(this, this.getContext());
        try {
            for (String key : bv) {
                Field field = getClass().getDeclaredField(key);
                if (key.equals("viewbinding")) {
                    viewbinding = (VB) field.get(this);
                } else {
                    viewmodel = (VM) field.get(this);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        if (viewbinding != null) {
            setContentView(viewbinding.getRoot());
        }
        initData();
        initView();
    }
    protected void initView() {}

    protected void initData(){}

    protected void onDismiss(){}

    protected void setDialogSize() {

        Window window = getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();

        WindowMetrics windowMetrics = window.getWindowManager().getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        int width = bounds.width();  // 屏幕宽度

        if (width > (int) (410 * getContext().getResources().getDisplayMetrics().density)) {
            width = (int) (410 * getContext().getResources().getDisplayMetrics().density);
        }

        wlp.width = width;

        wlp.gravity = Gravity.BOTTOM;

        window.setBackgroundDrawableResource(android.R.color.transparent);

        window.setAttributes(wlp);

    }

    @Override
    public void cancel() {
        super.cancel();
        onDismiss();
    }
}
