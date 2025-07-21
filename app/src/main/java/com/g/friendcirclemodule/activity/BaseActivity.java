package com.g.friendcirclemodule.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.interface_method.BaseBindingInterface;
import java.lang.reflect.Field;

public abstract class BaseActivity<VB extends ViewBinding,VM extends ViewModel> extends AppCompatActivity implements BaseBindingInterface<VB, VM> {

    final  String[] bv = {"viewbinding", "viewmodel"};
    public VB viewbinding;
    public VM viewmodel;
    public int sWidth;
    public View decorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
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
        updateBackgroundColor();
        initData();
        initView();
    }

    // 系统主题发生更改触发 onConfigurationChanged
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateBackgroundColor();
    }

    private void updateBackgroundColor() {
        boolean isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (isNightMode) {
            getWindow().getDecorView().setBackgroundColor(getColor(R.color.night_background));
        } else {
            getWindow().getDecorView().setBackgroundColor(getColor(R.color.light_background));
        }
    }

    // 设置自定义状态栏高度
    protected void adjustCustomStatusBar(View customStatusBar) {
        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) customStatusBar.getLayoutParams();
            params.topMargin = statusBarHeight;  // 下移自定义状态栏
            customStatusBar.setLayoutParams(params);
            return insets;
        });
    }

    protected void initView() {
        Point size = new Point();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        size.x = bounds.width();
        size.y = bounds.height();
        sWidth = Math.min(size.x, size.y);
    }

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
