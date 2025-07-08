package com.g.friendcirclemodule.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.databinding.CeRibItemBinding;
import com.g.friendcirclemodule.dp.EditDataManager;
import com.g.mediaselector.model.ResourceItem;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter<ResourceItem> {

    private OnItemClickListener onItemClickListener;
    private ExoPlayer player;

    public ImageGridAdapter(List<ResourceItem> mData) {
        this.mData = mData;
        Log.i("22222", String.valueOf(this.mData));
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
        vh.binding.playerView.setVisibility(View.GONE);
        vh.binding.ivImage.setVisibility(View.VISIBLE);
        vh.binding.videoTime.setVisibility(View.GONE);
        if (position == (this.mData.size())) {
            vh.binding.ivImage.setImageResource(R.mipmap.add);
        } else {
            ResourceItem item = this.mData.get(position);
            Log.i("type_1122", String.valueOf(item.type));
            if (item.type == ResourceItem.TYPE_IMAGE) {
                Glide.with(vh.binding.getRoot()).load(item.path).into(vh.binding.ivImage); // Glide加载
            } else {
                vh.binding.playerView.setVisibility(View.VISIBLE);
                vh.binding.videoTime.setVisibility(View.VISIBLE);
                vh.binding.ivImage.setVisibility(View.GONE);
                // 1. 初始化播放器
                player = new ExoPlayer.Builder(vh.binding.getRoot().getContext()).build();
                vh.binding.playerView.setPlayer(player);
                // 2. 设置媒体源（支持本地/网络URI）
                MediaItem mediaItem = MediaItem.fromUri(item.path);
                player.setMediaItem(mediaItem);
                player.prepare();
            }
        }
        // 点击事件
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

    // 停止当前播放的视频
    public void stopCurrentPlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }


}

