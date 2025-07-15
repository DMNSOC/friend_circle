package com.g.friendcirclemodule.adapter;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewPool {
    private static final int TYPE_ITEM = 1;
    private static final RecyclerView.RecycledViewPool sharedPool = new RecyclerView.RecycledViewPool();

    public static RecyclerView.RecycledViewPool getSharedPool() {
        sharedPool.setMaxRecycledViews(TYPE_ITEM, 20);
        return sharedPool;
    }

}
