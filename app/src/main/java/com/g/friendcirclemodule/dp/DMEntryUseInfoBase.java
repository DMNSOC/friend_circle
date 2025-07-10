package com.g.friendcirclemodule.dp;

public class DMEntryUseInfoBase {
    long id;
    int useId;
    String friendName;
    String friendHead;
    String friendBg;

    // id 1 为头像 2 为昵称 3 为背景
    public DMEntryUseInfoBase(long id, int useId, String friendName, String friendHead, String friendBg) {
        this.id = id;
        this.useId = useId;
        this.friendName = friendName;
        this.friendHead = friendHead;
        this.friendBg = friendBg;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUseId() {
        return useId;
    }

    public void setUseId(int useId) {
        this.useId = useId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendHead() {
        return friendHead;
    }

    public void setFriendHead(String friendHead) {
        this.friendHead = friendHead;
    }

    public String getFriendBg() {
        return friendBg;
    }

    public void setFriendBg(String friendBg) {
        this.friendBg = friendBg;
    }
}
