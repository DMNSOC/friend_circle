package com.g.friendcirclemodule.adapter;

import static com.g.friendcirclemodule.activity.MainActivity.hostActivity;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.databinding.CeRibItemBinding;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.model.ResourceItem;
import java.io.IOException;
import java.util.List;

public class MainImageGridAdapter extends BaseAdapter<ResourceItem> {

    private OnItemClickListener onItemClickListener;

    public MainImageGridAdapter(List<ResourceItem> mData) {
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
        ResourceItem item = mData.get(position);
        ViewGroup.LayoutParams params = vh.binding.ceRib.getLayoutParams();
        vh.binding.playerView.setVisibility(View.GONE);
        vh.binding.ivImage.setVisibility(View.VISIBLE);
        vh.binding.videoTime.setVisibility(View.GONE);
        if (item.type == ResourceItem.TYPE_VIDEO) {
            vh.binding.videoTime.setVisibility(View.VISIBLE);
            vh.binding.videoTime.setText(UtilityMethod.formatDuration(item.duration));
        }

        if (mData.size() == 1) {
            if (item.type == ResourceItem.TYPE_VIDEO) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(item.path); // 支持文件路径或Uri
                String widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                if (widthStr != null && heightStr != null) {
                    int width = Integer.parseInt(widthStr);
                    int height = Integer.parseInt(heightStr);
                    params.width = UtilityMethod.pxToDp(hostActivity.getBaseContext(), width * 2);
                    params.height = UtilityMethod.pxToDp(hostActivity.getBaseContext(), height * 2);
                }
                try {
                    retriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 仅解码尺寸
                BitmapFactory.decodeFile(item.path, options);
                int width = options.outWidth;
                int height = options.outHeight;
                params.width = UtilityMethod.pxToDp(hostActivity.getBaseContext(), width);
                params.height = UtilityMethod.pxToDp(hostActivity.getBaseContext(), height);
            }
        } else {
            params.width = UtilityMethod.dpToPx(hostActivity.getBaseContext(), 75);
            params.height = UtilityMethod.dpToPx(hostActivity.getBaseContext(), 75);
        }
        vh.binding.ceRib.setLayoutParams(params);

        Glide.with(vh.binding.getRoot())
                .load(item.path)
                .placeholder(R.mipmap.add)
                .into(vh.binding.ivImage); // Glide加载
        // 点击事件
        vh.binding.getRoot().setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickListener(view, position);
            }
        });
    }
    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(MainImageGridAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;  // 接收外部实现的监听器
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }
}
