package com.g.friendcirclemodule.interface_method;

import com.g.friendcirclemodule.databinding.FriendEntryBinding;
import com.g.mediaselector.model.ResourceItem;

import java.util.List;

public interface EnterImageUIProvider {
    void bindImageView(FriendEntryBinding itemView, List<ResourceItem> list);
    void dialogOnPlay();
    void dialogOnPause();
}