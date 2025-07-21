package com.g.friendcirclemodule.dp;

import androidx.viewbinding.ViewBinding;
import java.util.List;

public class AdapterVPBase { // viewbinding  pos
    public ViewBinding vb;
    public int pos;
    public List<?> mData;
    public AdapterVPBase(ViewBinding vb, int pos, List<?> mData) {
        this.vb = vb;
        this.pos = pos;
        this.mData = mData;
    }
}