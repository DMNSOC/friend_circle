package com.g.mediaselector.model;

import java.util.ArrayList;
import java.util.List;

public class ResourceFolder {
    public String name;
    public String path;
    public List<ResourceItem> items = new ArrayList<>();
    public String coverPath;

    public ResourceFolder(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
