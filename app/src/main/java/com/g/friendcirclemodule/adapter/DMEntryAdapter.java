package com.g.friendcirclemodule.adapter;

import static android.content.Context.WINDOW_SERVICE;
import static com.g.friendcirclemodule.activity.MainActivity.hostActivity;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.g.friendcirclemodule.R;
import com.g.friendcirclemodule.activity.BgReplaceMoreActivity;
import com.g.friendcirclemodule.databinding.MainFriendEntryBinding;
import com.g.friendcirclemodule.databinding.MainTopBinding;
import com.g.friendcirclemodule.dialog.PreviewDialog;
import com.g.friendcirclemodule.dp.DMEntryBase;
import com.g.friendcirclemodule.dp.DMEntryUseInfoBase;
import com.g.friendcirclemodule.dp.FeedManager;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import com.g.mediaselector.model.ResourceItem;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DMEntryAdapter extends BaseAdapter<DMEntryBase> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private OnItemClickListener onItemClickListener;
    public PreviewDialog dialog;
    MainImageGridAdapter adapter;

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

            // 设置缓存的封面
            List<DMEntryUseInfoBase> coverInfoBaseList = FeedManager.getUseInfo(3, 1);
            if (!coverInfoBaseList.isEmpty()) {
                DMEntryUseInfoBase dmEntryUseInfoBase = coverInfoBaseList.get(0);
                if (dmEntryUseInfoBase.getFriendBg() != "" && dmEntryUseInfoBase.getFriendBg() != null) {
                    Bitmap croppedBitmap = null;
                    try {
                        croppedBitmap = BitmapFactory.decodeStream(hvh.binding.getRoot().getContext().getContentResolver().openInputStream(Uri.parse(dmEntryUseInfoBase.getFriendBg())));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    hvh.binding.mainTopBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    hvh.binding.mainTopBg.setImageBitmap(croppedBitmap);
                } else {
                    hvh.binding.mainTopBg.setImageResource(R.mipmap.bz1);
                }
            } else {
                hvh.binding.mainTopBg.setImageResource(R.mipmap.bz1);
            }


            // 点击事件
            hvh.binding.getRoot().setOnClickListener(v -> {
                ViewGroup.LayoutParams params1 = hvh.binding.mainTop.getLayoutParams();
                ViewGroup.LayoutParams params2 = hvh.binding.mainTopBg.getLayoutParams();
                int dp = UtilityMethod.pxToDp(hvh.binding.getRoot().getContext(), params1.height);
                int px1 = 0;
                int px2 = 0;

                if (dp == 600) {
                    hvh.binding.mainImages.setVisibility(View.GONE);
                    hvh.binding.mainHeadName.setVisibility(View.VISIBLE);
                    px1 = UtilityMethod.dpToPx(hvh.binding.getRoot().getContext(), 300);
                    px2 = UtilityMethod.dpToPx(hvh.binding.getRoot().getContext(), 240);
                } else {
                    hvh.binding.mainHeadName.setVisibility(View.GONE);
                    hvh.binding.mainImages.setVisibility(View.VISIBLE);
                    px1 = UtilityMethod.dpToPx(hvh.binding.getRoot().getContext(), 600);
                    px2 = UtilityMethod.dpToPx(hvh.binding.getRoot().getContext(), 540);
                }
                Log.i("dddddddd", px1 + "=====" + px2 + "========" +hvh.binding.mainTop.getHeight());

                ValueAnimator heightAnim1 = ValueAnimator.ofInt(hvh.binding.mainTop.getHeight(), px1);
                heightAnim1.addUpdateListener(animation -> {
                    params1.height = (int) animation.getAnimatedValue();
                    hvh.binding.mainTop.setLayoutParams(params1);
                    hvh.binding.mainTop.requestLayout(); // 强制刷新布局
                });
                heightAnim1.setDuration(500).start();

                ValueAnimator heightAnim2 = ValueAnimator.ofInt(hvh.binding.mainTopBg.getHeight(), px2);
                heightAnim2.addUpdateListener(animation -> {
                    params2.height = (int) animation.getAnimatedValue();
                    hvh.binding.mainTopBg.setLayoutParams(params2);
                    hvh.binding.mainTopBg.requestLayout(); // 强制刷新布局
                });
                heightAnim2.setDuration(500).start();

            });

            hvh.binding.mainImages.setOnClickListener(v -> {

                Intent i = new Intent(hostActivity, BgReplaceMoreActivity.class);
                hostActivity.startActivity(i);
            });

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

            mfeb.binding.catalogsList.setVisibility(View.GONE);

            if (!Objects.equals(dmEntryBase.getLikesId(), "")) {
                String likesId = dmEntryBase.getLikesId();
                String[] likesArr = likesId.split(",");  // 按逗号分割
                if (likesArr.length > 0) {
                    mfeb.binding.catalogsList.setVisibility(View.VISIBLE);
                    String str = "";
                    Log.i("testtttt", str);
                    Log.i("testtttt", Arrays.toString(likesArr));
                    for (String s : likesArr) {
                        List<DMEntryUseInfoBase> NIBL = FeedManager.getUseInfo(2, Integer.parseInt(s));
                        Log.i("testtttt", str);
                        if (!NIBL.isEmpty()) {
                            Log.i("testtttt", str);
                            DMEntryUseInfoBase dmEntryUseInfoBase = NIBL.get(0);
                            if (dmEntryUseInfoBase.getFriendName() != "" && dmEntryUseInfoBase.getFriendName() != null) {
                                if (Objects.equals(str, "")) {
                                    str = dmEntryUseInfoBase.getFriendName();
                                } else {
                                    str = str + "、" + dmEntryUseInfoBase.getFriendName();
                                }
                            } else {
                                if (Objects.equals(str, "")) {
                                    str = holder.itemView.getContext().getString(R.string.user_name);
                                } else {
                                    str = str + "、" + holder.itemView.getContext().getString(R.string.user_name);
                                }
                            }
                        } else {
                            if (Objects.equals(str, "")) {
                                str = holder.itemView.getContext().getString(R.string.user_name);
                            } else {
                                str = str + "、" + holder.itemView.getContext().getString(R.string.user_name);
                            }
                        }
                    }
                    mfeb.binding.catalogsText.setText(str);
                }
            }

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

            View popupView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.more_dialog, null);

            if (dmEntryBase.getLikeState() == 1) {
                popupView.findViewById(R.id.like_text).setVisibility(View.GONE);
                popupView.findViewById(R.id.like_rse).setVisibility(View.VISIBLE);
            } else {
                popupView.findViewById(R.id.like_text).setVisibility(View.VISIBLE);
                popupView.findViewById(R.id.like_rse).setVisibility(View.GONE);
            }

            mfeb.binding.friendEntryMore.setOnClickListener(v -> {
                PopupWindow popup = new PopupWindow(popupView, 180, 250, true);
                popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.setTouchable(true);
                popup.setOutsideTouchable(true);
                popup.showAsDropDown(mfeb.binding.friendEntryMore, -120, 0);


                popupView.findViewById(R.id.more_like).setOnClickListener(v1 -> { // 点赞
                    int likeState = dmEntryBase.getLikeState();
                    String likesId = dmEntryBase.getLikesId();
                    String[] likesArr = likesId.split(",");  // 按逗号分割
                    StringBuilder likeStr = new StringBuilder();
                    if (likeState == 1) {
                        for (String s : likesArr) {
                            if (!Objects.equals(s, "1")) {
                                if (likeStr.length() == 0){
                                    likeStr = new StringBuilder(s);
                                } else {
                                    likeStr.append(",").append(s);
                                }
                            }
                        }
                        likeState = 0;
                    } else {
                        for (String s : likesArr) {
                            if (Objects.equals(s, "1")) {
                                if (likeStr.length() == 0){
                                    likeStr = new StringBuilder(s);
                                } else {
                                    likeStr.append(",").append(s);
                                }
                            }
                        }
                        likeStr.append(1);
                        likeState = 1;
                    }

                    DMEntryBase aeb = new DMEntryBase(dmEntryBase.getId(), dmEntryBase.getUseId(), dmEntryBase.getDecStr(), dmEntryBase.getFriendImageId(), dmEntryBase.getTime(), dmEntryBase.getFriendVideoId(), dmEntryBase.getFriendVideoTime(), likeState, likeStr.toString());
                    FeedManager.UpdateItemToAccounttb(aeb);

                    Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                    intent.putExtra("data_key", "更新数据");
                    LocalBroadcastManager.getInstance(holder.itemView.getContext()).sendBroadcast(intent);
                    popup.dismiss();

                });
                popupView.findViewById(R.id.more_delete).setOnClickListener(v1 -> { // 删除条目
                    int click_id = dmEntryBase.getId();
                    FeedManager.deleteItemFromAccounttbById(click_id);
                    Intent intent = new Intent("ACTION_DIALOG_CLOSED");
                    intent.putExtra("data_key", "更新数据");
                    LocalBroadcastManager.getInstance(holder.itemView.getContext()).sendBroadcast(intent);
                    popup.dismiss();
                });
            });


            adapter = new MainImageGridAdapter(list);
            mfeb.binding.mainRvImages.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position1) -> {
                Bundle bundle = new Bundle();
                bundle.putString("PATH", list.get(position1).path);
                bundle.putInt("TYPE", list.get(position1).type);
                Context context = hostActivity;
                dialog = new PreviewDialog(context, list, position1);
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
