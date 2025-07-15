package com.g.friendcirclemodule.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g.friendcirclemodule.databinding.FriendEntryBinding;
import com.g.friendcirclemodule.databinding.MainTopBinding;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.AdapterVPBase;
import com.g.friendcirclemodule.model.MainActivityModel;
import java.util.List;

public class DMEntryAdapter extends BaseAdapter<DMEntryBase> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    MainActivityModel viewmodel;

    public DMEntryAdapter(List<DMEntryBase> mData, MainActivityModel viewmodel) {
        this.viewmodel = viewmodel;
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final FriendEntryBinding binding;

        public ItemViewHolder(FriendEntryBinding mfeb) {
            super(mfeb.getRoot());
            this.binding = mfeb;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final MainTopBinding binding;

        public HeaderViewHolder(MainTopBinding mtb) {
            super(mtb.getRoot());
            this.binding = mtb;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            MainTopBinding mtb = MainTopBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new HeaderViewHolder(mtb);
        } else {
            FriendEntryBinding mfeb = FriendEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemViewHolder(mfeb);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            HeaderViewHolder hvh = (HeaderViewHolder)holder;
            AdapterVPBase base = new AdapterVPBase(hvh.binding, position, mData);
            viewmodel.setMainRecyclerBase(base);
        } else {
            ItemViewHolder mfeb = (ItemViewHolder)holder;
            AdapterVPBase base = new AdapterVPBase(mfeb.binding, position, mData);
            viewmodel.setMainRecyclerBase(base);
        }
    }
}
