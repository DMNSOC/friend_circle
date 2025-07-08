package com.g.friendcirclemodule.dp;

public class DMEntryUseInfoBase {
    long id;
    int useId;
    String friendName;
    String friendHead;

    // id 1 为头像 2 为昵称
    public DMEntryUseInfoBase(long id, int useId, String friendName, String friendHead) {
        this.id = id;
        this.useId = useId;
        this.friendName = friendName;
        this.friendHead = friendHead;

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
}
