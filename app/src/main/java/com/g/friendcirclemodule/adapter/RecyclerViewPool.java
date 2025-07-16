package com.g.friendcirclemodule.adapter;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewPool {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static RecyclerView.RecycledViewPool sharedPool = new RecyclerView.RecycledViewPool();

    public static RecyclerView.RecycledViewPool getSharedPool() {
        sharedPool.setMaxRecycledViews(TYPE_ITEM, 20);
        sharedPool.setMaxRecycledViews(TYPE_HEADER, 5);
        return sharedPool;
    }

}
