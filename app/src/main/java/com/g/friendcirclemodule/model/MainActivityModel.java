package com.g.friendcirclemodule.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.g.friendcirclemodule.dp.AdapterVPBase;

public class MainActivityModel extends BaseModel{
    private final MutableLiveData<AdapterVPBase> mainRecyclerBase = new MutableLiveData<>();
    private final MutableLiveData<AdapterVPBase> mainRecyclerImageGridBase = new MutableLiveData<>();
    // 设置数据主界面条目adapter数据
    public void setMainRecyclerBase(AdapterVPBase base) {
        mainRecyclerBase.setValue(base);
    }
    // 获取
    public LiveData<AdapterVPBase> getMainRecyclerBase() {
        return mainRecyclerBase;
    }
    // 设置数据主界面条目内的adapter数据
    public void setMainImageGridBase(AdapterVPBase base) {
        mainRecyclerImageGridBase.setValue(base);
    }
    // 获取
    public LiveData<AdapterVPBase> getMainImageGridBase() {
        return mainRecyclerImageGridBase;
    }
}
