package com.g.friendcirclemodule.dp;

import com.g.mediaselector.model.ResourceItem;
import java.util.ArrayList;
import java.util.List;

public class EditDataManager {
    public static List<ResourceItem> list = new ArrayList<>();

    public static List<ResourceItem> getList() {
        return list;
    }

    public static void setList(List<ResourceItem> l) {
        list = l;
    }

    public static void addList(List<ResourceItem> l) {
        list.addAll(l);
    }

    public static void onItemMove(int fromPosition, int toPosition) {
        // 排序时交换数据
        ResourceItem fromImage = list.get(fromPosition);
        list.remove(fromPosition);
        list.add(toPosition, fromImage);
    }

    public static void removeItem(int position) {
        list.remove(position);
    }
}
