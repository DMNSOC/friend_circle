package com.g.mediaselector.interface_method;

import com.g.mediaselector.model.ResourceItem;
import java.util.List;

public interface OnResourceSelectListener {
    void onSelected(List<ResourceItem> selectedList);
}