package com.g.friendcirclemodule.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;
import com.g.friendcirclemodule.interface_method.BaseBindingInterface;
import java.lang.reflect.Field;

public abstract class BaseActivity<VB extends ViewBinding,VM extends ViewModel> extends AppCompatActivity implements BaseBindingInterface<VB, VM> {

    final  String[] bv = {"viewbinding", "viewmodel"};
    public VB viewbinding;
    public VM viewmodel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewBinding(this, this.getBaseContext());
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
    // 获取系统状态栏高度
    int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // 设置自定义状态栏高度
    protected void adjustCustomStatusBar(View customStatusBar) {
        // 获取系统状态栏高度
        int statusBarHeight = getStatusBarHeight();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) customStatusBar.getLayoutParams();
        params.topMargin = statusBarHeight;  // 下移自定义状态栏
        customStatusBar.setLayoutParams(params);
    }

    protected void initView() {}

    protected void initInsets(View rootView) {

        ViewCompat.setOnApplyWindowInsetsListener(rootView, new androidx.core.view.OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                // 获取系统栏的 insets
                int insetLeft = insets.getInsets(WindowInsetsCompat.Type.systemBars()).left;
                int insetTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                int insetRight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).right;
                int insetBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                // 设置 padding，防止内容被遮挡
                v.setPadding(insetLeft, 0, insetRight, 0);

                return insets;
            }
        });

        // 触发一次 insets 分发（可选）
        rootView.requestApplyInsets();
    }

    protected void initData(){}

    protected void onResume() {
        super.onResume();
    }
}
