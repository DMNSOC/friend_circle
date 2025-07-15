package com.g.friendcirclemodule.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.g.friendcirclemodule.databinding.CeRibItemBinding;
import com.g.friendcirclemodule.dp.AdapterVPBase;
import com.g.friendcirclemodule.dp.EditDataManager;
import com.g.friendcirclemodule.model.ContentEditingActivityModel;
import com.g.mediaselector.model.ResourceItem;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter<ResourceItem> {

    private OnItemClickListener onItemClickListener;
    ContentEditingActivityModel viewmodel;

    public ImageGridAdapter(List<ResourceItem> mData, ContentEditingActivityModel viewmodel) {
        this.viewmodel = viewmodel;
        this.mData = mData;
    }

    // 主ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
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
        return new ViewHolder(ceribb);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder)holder;

        AdapterVPBase base = new AdapterVPBase(vh.binding, position, mData);
        viewmodel.setImageGridBase(base);
//        // 点击事件
        vh.binding.getRoot().setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickListener(vh);
            }
        });
    }
    public interface OnItemClickListener {
        void onItemClickListener(ViewHolder vh);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;  // 接收外部实现的监听器
    }
    public void onItemMove(int fromPosition, int toPosition) {
        if (toPosition == this.mData.size()) {
            return;
        }
        // 拖动排序时交换数据
        EditDataManager.onItemMove(fromPosition, toPosition);
        ResourceItem fromImage = this.mData.get(fromPosition);
        this.mData.remove(fromPosition);
        this.mData.add(toPosition, fromImage);
        notifyItemMoved(fromPosition, toPosition);
    }

    public ResourceItem getItem(int position) {
        return this.mData.get(position);
    }

    public List<ResourceItem> getData() {
        return this.mData;
    }

    public void removeItem(int position) {
        this.mData.remove(position);
        EditDataManager.removeItem(position);
        notifyItemRemoved(position);
    }

}

