package com.g.mediaselector.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.g.mediaselector.adapter.ResourceAdapter;
import com.g.mediaselector.dialog.FolderListDialog;
import com.g.mediaselector.interface_method.OnResourceSelectListener;
import com.g.mediaselector.interface_method.ResourceUIProvider;
import com.g.mediaselector.model.ResourceFolder;
import com.g.mediaselector.model.ResourceItem;
import com.g.mediaselector.utils.MediaStoreUtils;
import com.g.mediaselector.utils.PermissionUtils;
import com.g.mediaselector.databinding.ActivityResourcePickerBinding;
import com.g.mediaselector.databinding.ToolbarBinding;
import java.util.ArrayList;
import java.util.List;

public class ResourcePickerActivity extends AppCompatActivity {

    public static OnResourceSelectListener staticListener;
    public static ResourceUIProvider staticUIProvider;
    public static int staticMode = 1;
    public static boolean staticMulti = false;
    private final List<ResourceItem> selected = new ArrayList<>();
    private List<ResourceItem> allItems = new ArrayList<>();
    private List<ResourceFolder> folderList = new ArrayList<>();
    private ResourceFolder currentFolder;
    ActivityResourcePickerBinding arpb;
    ToolbarBinding tb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size); // 包含导航栏和状态栏
        int itemNum = 4;

        itemNum = (int) Math.floor((double) size.x / 300);

        arpb = ActivityResourcePickerBinding.inflate(getLayoutInflater());
        setContentView(arpb.getRoot());
        arpb.recyclerView.setLayoutManager(new GridLayoutManager(this, itemNum));
        arpb.btnDone.setOnClickListener(v -> finishWithResult());

        tb = ToolbarBinding.inflate(getLayoutInflater());
        // 文件夹选择
        tb.btnFile.setOnClickListener(v -> showFolderDialog());
        arpb.toolbarContainer.setVisibility(View.VISIBLE);
        arpb.toolbarContainer.addView(tb.getRoot());
        arpb.bottomBar.setVisibility(staticMulti? View.VISIBLE : View.GONE);
        tb.btnCancel.setOnClickListener(v -> finish());
        if (!PermissionUtils.hasStoragePermission(this)) {
            PermissionUtils.requestStoragePermission(this);
            return;
        }
        loadData();
    }

    private void loadData() {
        // 读取所有资源及文件夹分组
        folderList = MediaStoreUtils.getFolders(this, staticMode);
        if (!folderList.isEmpty()) {
            currentFolder = folderList.get(0);
            allItems = currentFolder.items;
            tb.topBarName.setText(currentFolder.name);
        }
        setAdapterData(allItems);
        // 完成按钮
        arpb.btnDone.setOnClickListener(v -> finishWithResult());
    }

    private void setAdapterData(List<ResourceItem> data) {
        ResourceAdapter adapter = new ResourceAdapter(data, staticUIProvider, selected);
        arpb.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //点击事件
        adapter.setOnItemClickListener((view, position) -> {
            ResourceItem item = data.get(position);
            if (staticMulti) {
                if (selected.contains(item)) selected.remove(item);
                else selected.add(item);
                adapter.notifyItemChanged(position);
            } else {
                selected.clear();
                selected.add(item);
                finishWithResult();
            }
        });
    }

    // 文件夹弹窗
    private void showFolderDialog() {
        FolderListDialog dialog = FolderListDialog.newInstance(folderList);
        dialog.setOnFolderSelectedListener(folder -> {
            currentFolder = folder;
            tb.topBarName.setText(folder.name);
            setAdapterData(currentFolder.items);
        });
        dialog.show(getSupportFragmentManager(), "folderDialog");
    }
    private void finishWithResult() {
        if (staticListener != null) {
            staticListener.onSelected(new ArrayList<>(selected));
        }
        finish();
    }
}