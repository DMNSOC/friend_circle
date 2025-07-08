package com.g.friendcirclemodule.adapter;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<T> mData;
    public int getItemCount() {
        return mData.size() + 1;
    }

}
