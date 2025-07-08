package com.g.friendcirclemodule.dialog;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.g.friendcirclemodule.activity.HeadSettingActivity;
import com.g.friendcirclemodule.databinding.SettingDialogBinding;
import com.g.friendcirclemodule.model.BaseModel;

public class SettingDialog extends BaseDialog<SettingDialogBinding, BaseModel>  {
    public SettingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        super.initView();
        viewbinding.settingSetHead.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), HeadSettingActivity.class);
            this.getContext().startActivity(i);
            cancel();
        });
        viewbinding.settingSetName.setOnClickListener(v -> {
            SetNameDialog dialog = new SetNameDialog(this.getContext());
            dialog.show();
            dialog.setDialogSize();
            cancel();
        });
    }
}