package com.g.mediaselector.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.g.mediaselector.R;
import com.g.mediaselector.model.ResourceFolder;
import com.g.mediaselector.databinding.ItemFolderBinding;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<ResourceFolder> data;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public FolderAdapter(List<ResourceFolder> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFolderBinding ifb = ItemFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(ifb);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        ResourceFolder folder = data.get(position);

        holder.binding.tvName.setText(folder.name);
        holder.binding.tvCount.setText(folder.items.size() + "项");
        // 用Glide或Picasso加载封面
         Glide.with(holder.binding.ivCover.getContext()).load(folder.coverPath).into(holder.binding.ivCover);

        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {
        private final ItemFolderBinding binding;
        FolderViewHolder(ItemFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}