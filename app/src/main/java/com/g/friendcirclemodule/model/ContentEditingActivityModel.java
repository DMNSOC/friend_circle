package com.g.friendcirclemodule.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.g.friendcirclemodule.dp.AdapterVPBase;

public class ContentEditingActivityModel extends BaseModel{
    private final MutableLiveData<AdapterVPBase> recyclerImageGridBase = new MutableLiveData<>();
    // 设置界面内的adapter数据
    public void setImageGridBase(AdapterVPBase base) {
        recyclerImageGridBase.setValue(base);
    }
    // 获取
    public LiveData<AdapterVPBase> getImageGridBase() {
        return recyclerImageGridBase;
    }
}
