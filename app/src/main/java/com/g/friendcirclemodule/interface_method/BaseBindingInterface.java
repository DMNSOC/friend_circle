package com.g.friendcirclemodule.interface_method;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.core.app.DialogCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface BaseBindingInterface<VB extends ViewBinding,VM extends ViewModel> {
    Class[] classArray = {LayoutInflater.class};
    String[] bv = {"viewbinding", "viewmodel"};
    default void initViewBinding(Object instance, Context context) {
        try {
            Context mContext = null;
            if (instance instanceof Fragment) {
                mContext = ((Fragment) instance).getContext();
            } else {
                if (instance instanceof Context) {
                    mContext = (Context) instance;
                }
            }
            Type type = instance.getClass().getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) type;
            for (String key : bv) {
                Object result = null;
                Field binding = instance.getClass().getField(key);
                if (key.equals("viewbinding")) {
                    Class<VB> vbClass = null;
                    if (pt != null) {
                        if (pt.getActualTypeArguments()[0] instanceof Class) {
                            vbClass = (Class<VB>) pt.getActualTypeArguments()[0];
                        }
                    }
                    Method inflate;
                    if (vbClass != null) {
                        inflate = vbClass.getMethod("inflate", classArray);
                        result = inflate.invoke(null, LayoutInflater.from(context));
                    }
                } else {
                    Class<VM> vmClass = null;
                    if (pt != null) {
                        vmClass = (Class<VM>) pt.getActualTypeArguments()[1];
                        if (pt.getActualTypeArguments()[0] instanceof Class) {
                            vmClass = (Class<VM>) pt.getActualTypeArguments()[1];
                        }
                    }
                    Log.i("222222", String.valueOf(vmClass));
                    if (vmClass != null) {
                        FragmentActivity mActivity = null;
                        if (instance instanceof Fragment) {
                            mActivity = ((Fragment) instance).getActivity();
                        } else {
                            mActivity = (FragmentActivity) mContext;
                        }
                        if (mActivity != null) {
                            result = new ViewModelProvider(mActivity).get(vmClass);
                        }
                    }
                }
                if (result != null) {
                    Log.i("222222", String.valueOf(result));
                    binding.setAccessible(true);
                    binding.set(instance, result);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
