package com.g.mediaselector.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.g.mediaselector.R;
import com.g.mediaselector.adapter.FolderAdapter;
import com.g.mediaselector.databinding.ItemFolderBinding;
import com.g.mediaselector.model.ResourceFolder;
import com.g.mediaselector.databinding.DialogFolderListBinding;
import java.util.List;

public class FolderListDialog extends DialogFragment {

    private List<ResourceFolder> folderList;
    private FolderAdapter.OnItemClickListener onItemClickListener;
    private OnFolderSelectedListener onFolderSelectedListener;

    public interface OnFolderSelectedListener {
        void onFolderSelected(ResourceFolder folder);
    }
    public void setOnFolderSelectedListener(OnFolderSelectedListener l) {
        onFolderSelectedListener = l;
    }

    public static FolderListDialog newInstance(List<ResourceFolder> folders) {
        FolderListDialog d = new FolderListDialog();
        d.folderList = folders;
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), com.github.chrisbanes.photoview.R.style.Theme_AppCompat_Light_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogFolderListBinding dflb = DialogFolderListBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialog.setContentView(dflb.getRoot());
        RecyclerView recyclerView = dflb.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FolderAdapter adapter = new FolderAdapter(folderList, position -> {
            if (onFolderSelectedListener != null) {
                onFolderSelectedListener.onFolderSelected(folderList.get(position));
            }
            dismissAllowingStateLoss();
        });
        recyclerView.setAdapter(adapter);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
        return dialog;
    }
}