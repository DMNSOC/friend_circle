package com.g.friendcirclemodule.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    protected void initView() {}

    protected void initData(){}

    protected void onResume() {
        super.onResume();
    }
}
