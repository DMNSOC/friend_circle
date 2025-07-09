package com.g.friendcirclemodule.adapter;

import static android.content.Context.WINDOW_SERVICE;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.activity.MainActivity;
import com.g.friendcirclemodule.databinding.MainFriendEntryBinding;
import com.g.friendcirclemodule.databinding.MainTopBinding;
import com.g.friendcirclemodule.dialog.PreviewDialog;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.mediaselector.model.ResourceItem;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DMEntryAdapter extends BaseAdapter<DMEntryBase> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private OnItemClickListener onItemClickListener;

    public DMEntryAdapter(List<DMEntryBase> mData) {
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final MainFriendEntryBinding binding;

        public ItemViewHolder(MainFriendEntryBinding mfeb) {
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
            MainFriendEntryBinding mfeb = MainFriendEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemViewHolder(mfeb);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            HeaderViewHolder hvh = (HeaderViewHolder)holder;
            // 设置缓存的头像信息
            List<DMEntryUseInfoBase> headInfoBaseList = FeedManager.getUseInfo(1, 1);
            if (!headInfoBaseList.isEmpty()) {
                DMEntryUseInfoBase dmEntryUseInfoBase = headInfoBaseList.get(0);
                if (dmEntryUseInfoBase.getFriendHead() != "" && dmEntryUseInfoBase.getFriendHead() != null) {
                    Bitmap croppedBitmap = null;
                    try {
                        croppedBitmap = BitmapFactory.decodeStream(hvh.binding.getRoot().getContext().getContentResolver().openInputStream(Uri.parse(dmEntryUseInfoBase.getFriendHead())));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    hvh.binding.mainTopTx.setImageBitmap(croppedBitmap);
                } else {
                    hvh.binding.mainTopTx.setImageResource(R.mipmap.tx);
                }
            } else {
                hvh.binding.mainTopTx.setImageResource(R.mipmap.tx);
            }

            // 设置缓存的名称信息
            List<DMEntryUseInfoBase> nameInfoBaseList = FeedManager.getUseInfo(2, 1);
            if (!nameInfoBaseList.isEmpty()) {
                DMEntryUseInfoBase dmEntryUseInfoBase = nameInfoBaseList.get(0);
                if (dmEntryUseInfoBase.getFriendName() != "" && dmEntryUseInfoBase.getFriendName() != null) {
                    hvh.binding.mainTopName.setText(dmEntryUseInfoBase.getFriendName());
                } else {
                    hvh.binding.mainTopName.setText(R.string.user_name);
                }
            } else {
                hvh.binding.mainTopName.setText(R.string.user_name);
            }

            // 点击事件
            hvh.binding.mainTopTx.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(hvh);
                }
            });

        } else {

            ItemViewHolder mfeb = (ItemViewHolder)holder;

            WindowManager wm = (WindowManager) mfeb.binding.getRoot().getContext().getSystemService(WINDOW_SERVICE);
            Point size = new Point();
            wm.getDefaultDisplay().getRealSize(size); // 包含导航栏和状态栏
            int width = size.x;

            if (size.x < size.y) {
                width = size.x;
            } else {
                width = size.y;
            }

            ViewGroup.LayoutParams params = mfeb.binding.dmeaMain.getLayoutParams();
            params.width = width; // 设置高度为200像素
            mfeb.binding.dmeaMain.setLayoutParams(params);

            DMEntryBase dmEntryBase = mData.get(position - 1);
            // 设置缓存的头像信息
            List<DMEntryUseInfoBase> headInfoBaseList = FeedManager.getUseInfo(1, dmEntryBase.getUseId());
            if (!headInfoBaseList.isEmpty()) {
                DMEntryUseInfoBase dmEntryUseInfoBase = headInfoBaseList.get(0);
                if (!Objects.equals(dmEntryUseInfoBase.getFriendHead(), "") && dmEntryUseInfoBase.getFriendHead() != null) {
                    Bitmap croppedBitmap = null;
                    try {
                        croppedBitmap = BitmapFactory.decodeStream(mfeb.binding.getRoot().getContext().getContentResolver().openInputStream(Uri.parse(dmEntryUseInfoBase.getFriendHead())));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    mfeb.binding.friendEntryHead.setImageBitmap(croppedBitmap);
                } else {
                    mfeb.binding.friendEntryHead.setImageResource(R.mipmap.tx);
                }
            } else {
                mfeb.binding.friendEntryHead.setImageResource(R.mipmap.tx);
            }
            // 设置缓存的名称信息
            List<DMEntryUseInfoBase> nameInfoBaseList = FeedManager.getUseInfo(2, dmEntryBase.getUseId());
            if (!nameInfoBaseList.isEmpty()) {
                DMEntryUseInfoBase dmEntryUseInfoBase = nameInfoBaseList.get(0);
                if (!Objects.equals(dmEntryUseInfoBase.getFriendName(), "") && dmEntryUseInfoBase.getFriendName() != null) {
                    mfeb.binding.friendEntryName.setText(dmEntryUseInfoBase.getFriendName());
                } else {
                    mfeb.binding.friendEntryName.setText(R.string.user_name);
                }
            } else {
                mfeb.binding.friendEntryName.setText(R.string.user_name);
            }
            if (Objects.equals(dmEntryBase.getDecStr(), "")) {
                mfeb.binding.friendEntryDec.setVisibility(View.GONE);
            } else {
                mfeb.binding.friendEntryDec.setVisibility(View.VISIBLE);
                mfeb.binding.friendEntryDec.setText(dmEntryBase.getDecStr());
            }
            mfeb.binding.friendEntryTime.setText(mfeb.binding.getRoot().getContext().getString(R.string.entry_time, String.valueOf(dmEntryBase.getTime())));
            mfeb.binding.mainRvImages.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 3));

            String imageStr = dmEntryBase.getFriendImageId();
            String[] imageArr = imageStr.split(",");  // 按逗号分割
            List<ResourceItem> list = new ArrayList<>();
            if (!imageStr.equals("") && imageArr.length >= 1) {
                for (String s : imageArr) {
                    ResourceItem a = new ResourceItem(1, s, ResourceItem.TYPE_IMAGE,0, null);
                    list.add(a);
                }
            }
            String videoStr = dmEntryBase.getFriendVideoId();
            String[] videoArr = videoStr.split(",");  // 按逗号分割
            String videoTimeStr = dmEntryBase.getFriendVideoTime();
            String[] videoTimeArr = videoTimeStr.split(",");  // 按逗号分割
            if (!videoStr.equals("") && videoArr.length >= 1) {
                for (int i = 0; i < videoArr.length; i++) {
                    ResourceItem a = new ResourceItem(2, videoArr[i], ResourceItem.TYPE_VIDEO,Long.parseLong(videoTimeArr[i]), null);
                    list.add(a);
                }
            }

            MainImageGridAdapter adapter = new MainImageGridAdapter(list);
            mfeb.binding.mainRvImages.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position1) -> {
                Bundle bundle = new Bundle();
                bundle.putString("PATH", list.get(position1).path);
                bundle.putInt("TYPE", list.get(position1).type);
                Context context = MainActivity.hostActivity;
                PreviewDialog dialog = new PreviewDialog(context, list, position1);
                dialog.show();
                dialog.setDialogSize();
            });
            adapter.notifyDataSetChanged();

        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(HeaderViewHolder hvh);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;  // 接收外部实现的监听器
    }
}
