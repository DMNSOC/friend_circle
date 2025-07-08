package com.g.friendcirclemodule.adapter;

import android.content.Context;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.databinding.ItemPreviewImageBinding;
import com.g.friendcirclemodule.databinding.ItemPreviewVideoBinding;
import com.g.friendcirclemodule.dialog.PDPlayerBase;
import com.g.mediaselector.model.ResourceItem;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ResourceItem> data;
    Context context;
    List<PDPlayerBase> playerList = new ArrayList<>();

    public PreviewPagerAdapter(Context ctx, List<ResourceItem> data) {
        this.context = ctx;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ResourceItem.TYPE_VIDEO) {
            ItemPreviewVideoBinding binding = ItemPreviewVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new VideoHolder(binding);
        } else {
            ItemPreviewImageBinding binding = ItemPreviewImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ImageHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ResourceItem item = data.get(position);
        if (holder instanceof ImageHolder) {
            ImageHolder ih = (ImageHolder) holder;
            Glide.with(ih.binding.getRoot()).load(item.path).placeholder(R.mipmap.add).apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(ih.binding.photoView);
        } else if (holder instanceof VideoHolder) {
            VideoHolder vh = (VideoHolder) holder;
            // 1. 初始化播放器
            ExoPlayer player = new ExoPlayer.Builder(vh.binding.getRoot().getContext()).build();
            vh.binding.videoView.setPlayer(player);
            // 2. 设置媒体源（支持本地/网络URI）
            MediaItem mediaItem = MediaItem.fromUri(item.path);
            player.setMediaItem(mediaItem);
            player.prepare();
            playerList.add(new PDPlayerBase(player, position));
            if (position != 0) {
                playVideoAtPosition(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ImageHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;
        ItemPreviewImageBinding binding;
        ImageHolder(ItemPreviewImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            photoView = itemView.findViewById(R.id.photoView);
        }
    }

    static class VideoHolder extends RecyclerView.ViewHolder {
        ItemPreviewVideoBinding binding;
        VideoHolder(ItemPreviewVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void playVideoAtPosition(int pos) {
        for (PDPlayerBase exoPlayer : playerList) {
            if (exoPlayer.pos == pos) {
                exoPlayer.exoPlayer.prepare();
                exoPlayer.exoPlayer.play();
            }
        }
    }

    public void pauseCurrentVideo(int pos) {
        for (PDPlayerBase exoPlayer : playerList) {
            if (exoPlayer.pos == pos) {
                exoPlayer.exoPlayer.stop();
            }
        }
    }
    public void stopCurrentPlayer() {
        for (PDPlayerBase exoPlayer : playerList) {
            exoPlayer.exoPlayer.stop();
            exoPlayer.exoPlayer.release();
        }
    }
}