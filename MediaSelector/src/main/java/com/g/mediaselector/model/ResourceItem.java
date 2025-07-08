package com.g.mediaselector.model;

import android.net.Uri;

public class ResourceItem {
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    public long id;
    public String path;
    public int type; // 1:图片 2:视频
    public long duration; // 视频时长（毫秒）

    public Uri uri;

    public ResourceItem(long id, String path, int type, long duration, Uri uri) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.duration = duration;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}