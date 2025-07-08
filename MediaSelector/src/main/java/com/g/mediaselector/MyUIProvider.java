package com.g.mediaselector;

import com.g.mediaselector.utils.MediaStoreUtils;
import android.view.View;
import com.bumptech.glide.Glide;
import com.g.mediaselector.databinding.ItemRibBinding;
import com.g.mediaselector.interface_method.ResourceUIProvider;
import com.g.mediaselector.model.ResourceItem;

public class MyUIProvider implements ResourceUIProvider {

    @Override
    public void bindItemView(ItemRibBinding binding, ResourceItem item, boolean selected) {

        Glide.with(binding.getRoot()).load(item.path).into(binding.ivThumb); // Glide加载
        binding.checkView.setSelected(selected);
        if (item.type == ResourceItem.TYPE_VIDEO) {
            binding.tvDuration.setText(MediaStoreUtils.formatDuration(item.duration));
            binding.tvDuration.setVisibility(View.VISIBLE);
        }
    }

}
