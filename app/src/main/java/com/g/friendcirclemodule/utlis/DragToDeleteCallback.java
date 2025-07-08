package com.g.friendcirclemodule.utlis;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.g.friendcirclemodule.adapter.ImageGridAdapter;

public class DragToDeleteCallback extends ItemTouchHelper.Callback {

    private final ImageGridAdapter adapter;
    private boolean isOverDeleteArea = false;
    private final View deleteArea;

    public DragToDeleteCallback(ImageGridAdapter adapter, View deleteArea) {
        this.adapter = adapter;
        this.deleteArea = deleteArea;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        if (viewHolder.getBindingAdapterPosition() != (adapter.getItemCount() - 1)) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        }
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // 交换数据
        adapter.onItemMove(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = 2000;
        recyclerView.setLayoutParams(params);
        // 检测拖拽项是否进入删除区域
        int[] deleteAreaLocation = new int[2];
        deleteArea.getLocationOnScreen(deleteAreaLocation);

        int[] itemLocation = new int[2];
        viewHolder.itemView.getLocationOnScreen(itemLocation);

        int itemX = itemLocation[0] + viewHolder.itemView.getWidth() / 2;
        int itemY = itemLocation[1] + viewHolder.itemView.getHeight() / 2;

        // 判断是否在删除区域
        Log.i("idddd", String.valueOf(isOverDeleteArea));
        if (isCurrentlyActive) {
            deleteArea.setVisibility(View.VISIBLE);
            isOverDeleteArea = ((itemX >= deleteAreaLocation[0]) &&
                    (itemX <= deleteAreaLocation[0] + deleteArea.getWidth()) &&
                    (itemY >= deleteAreaLocation[1]) &&
                    (itemY <= deleteAreaLocation[1] + deleteArea.getHeight())
            );
            if (isOverDeleteArea) {
                deleteArea.setBackgroundColor(0x98FF0000); // 红色高亮
            } else {
                deleteArea.setBackgroundColor(0x7D7D7D84); // 恢复默认颜色
            }
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        Log.i("idddd", String.valueOf(viewHolder.getBindingAdapterPosition()));
        Log.i("idddd", String.valueOf(isOverDeleteArea));
        deleteArea.setVisibility(View.GONE);
        if (isOverDeleteArea) {
            adapter.removeItem(viewHolder.getBindingAdapterPosition());
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return super.isLongPressDragEnabled();
    }

}
