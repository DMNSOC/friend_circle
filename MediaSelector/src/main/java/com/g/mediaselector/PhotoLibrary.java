package com.g.mediaselector;

import android.content.Context;
import android.content.Intent;
import com.g.mediaselector.activity.ResourcePickerActivity;
import com.g.mediaselector.interface_method.OnResourceSelectListener;
import com.g.mediaselector.interface_method.ResourceUIProvider;

public class PhotoLibrary {

    public static final int MODE_IMAGE = 1;
    public static final int MODE_VIDEO = 2;
    public static final int MODE_ALL = 3;

    public static class Builder {
        private final Context context;
        private int mode = MODE_IMAGE;
        private boolean multiSelect = false;
        private int selectNum = 0;
        private ResourceUIProvider uiProvider;
        private OnResourceSelectListener selectListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMode(int mode) {
            this.mode = mode;
            return this;
        }

        public Builder setMultiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }
        public Builder setSelectNum(int selectNum) {
            this.selectNum = selectNum;
            return this;
        }
        public Builder setUIProvider(ResourceUIProvider provider) {
            this.uiProvider = provider;
            return this;
        }

        public Builder setSelectListener(OnResourceSelectListener listener) {
            this.selectListener = listener;
            return this;
        }

        public void open() {
            ResourcePickerActivity.staticListener = selectListener;
            ResourcePickerActivity.staticUIProvider = uiProvider;
            ResourcePickerActivity.staticMode = mode;
            ResourcePickerActivity.staticMulti = multiSelect;
            ResourcePickerActivity.selectNum = selectNum;
            Intent i = new Intent(context, ResourcePickerActivity.class);
            context.startActivity(i);
        }
    }
}