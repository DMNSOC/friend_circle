package com.g.mediaselector.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.g.mediaselector.interface_method.OnItemClickListener;
import com.g.mediaselector.model.ResourceItem;
import com.g.mediaselector.interface_method.ResourceUIProvider;
import com.g.mediaselector.databinding.ItemRibBinding;
import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ResourceItem> data;
    private final ResourceUIProvider uiProvider;
    private final List<ResourceItem> selected;

    public ResourceAdapter(List<ResourceItem> data, ResourceUIProvider uiProvider, List<ResourceItem> selected) {
        this.data = data;
        this.uiProvider = uiProvider;
        this.selected = selected;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }
    public static class RIBViewHolder extends RecyclerView.ViewHolder {

        private final ItemRibBinding binding;
        public RIBViewHolder(ItemRibBinding rib) {
            super(rib.getRoot());
            this.binding = rib;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRibBinding rib = ItemRibBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RIBViewHolder(rib);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        RIBViewHolder rib = (RIBViewHolder)holder;
        ResourceItem item = data.get(position);
        boolean isSelected = selected.contains(item);
        uiProvider.bindItemView(rib.binding, item, isSelected);
        // 点击事件
        rib.binding.getRoot().setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;  // 接收外部实现的监听器
    }
}
