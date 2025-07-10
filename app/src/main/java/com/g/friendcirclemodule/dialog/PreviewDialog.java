package com.g.friendcirclemodule.dialog;

import android.content.Context;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import com.g.friendcirclemodule.adapter.PreviewPagerAdapter;
import com.g.friendcirclemodule.databinding.PreviewDialogBinding;
import com.g.friendcirclemodule.model.BaseModel;
import com.g.mediaselector.model.ResourceItem;
import java.util.List;
import java.util.Objects;

public class PreviewDialog extends BaseDialog<PreviewDialogBinding, BaseModel> {
    private final List<ResourceItem> dataList;
    private final int startPosition;
    private final Context context;
    PreviewPagerAdapter adapter;
    int oldPosition = -1;
    public PreviewDialog(@NonNull Context context, List<ResourceItem> dataList, int startPosition) {
        super(context);
        this.context = context;
        this.dataList = dataList;
        this.startPosition = startPosition;
    }

    @Override
    protected void initView() {
        super.initView();
        adapter = new PreviewPagerAdapter(context, dataList);

        viewbinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
                // 切换到新页面时播放视频\
                if (oldPosition != -1) {
                    adapter.pauseCurrentVideo(oldPosition);
                }
                adapter.playVideoAtPosition(position);
                oldPosition = position;
            }
        });

        updateIndicator(startPosition);
        viewbinding.viewPager.setAdapter(adapter);
        viewbinding.viewPager.setCurrentItem(startPosition, false);

        viewbinding.btnClose.setOnClickListener(v -> {
            cancel();
        });
    }


    @Override
    public void setDialogSize() {
        Objects.requireNonNull(getWindow()).setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );
    }

    private void updateIndicator(int pos) {
        viewbinding.tvIndicator.setText((pos + 1) + "/" + dataList.size());
    }
    protected void onDismiss() {
        adapter.stopCurrentPlayer();
    }
}