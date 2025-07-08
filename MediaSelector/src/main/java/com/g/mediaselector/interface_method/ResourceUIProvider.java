package com.g.mediaselector.interface_method;

import com.g.mediaselector.databinding.ItemRibBinding;
import com.g.mediaselector.model.ResourceItem;

public interface ResourceUIProvider {
    void bindItemView(ItemRibBinding itemView, ResourceItem item, boolean selected);
}
