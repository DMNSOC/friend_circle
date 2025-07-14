package com.g.friendcirclemodule.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g.friendcirclemodule.activity.MainActivity;
import com.g.friendcirclemodule.databinding.CeRibItemBinding;
import com.g.friendcirclemodule.dp.AdapterVPBase;
import com.g.friendcirclemodule.model.MainActivityModel;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.model.ResourceItem;
import java.util.List;

public class MainImageGridAdapter extends BaseAdapter<ResourceItem> {
    MainActivityModel viewmodel;

    public MainImageGridAdapter(List<ResourceItem> mData, MainActivityModel viewmodel) {
        this.viewmodel = viewmodel;
        this.mData = mData;
    }

    // 主ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CeRibItemBinding binding;
        ViewHolder(CeRibItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CeRibItemBinding ceribb = CeRibItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MainImageGridAdapter.ViewHolder(ceribb);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MainImageGridAdapter.ViewHolder vh = (MainImageGridAdapter.ViewHolder)holder;

        AdapterVPBase base = new AdapterVPBase(vh.binding, position, mData);
        viewmodel.setMainImageGridBase(base);

    }
    @Override
    public int getItemCount() {
        return this.mData.size();
    }
}
