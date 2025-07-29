package com.g.friendcirclemodule.dp;

import java.util.List;

public class DMEntryBase {
    int id;
    int useId;
    String decStr;
    String friendImageId;
    String time;
    String friendVideoId;
    String friendVideoTime;
    String likesId;
    List<CommentBase> commentList;

    public DMEntryBase(int id, int useId, String decStr, String friendImageId, String time, String friendVideoId, String friendVideoTime, String likesId, List<CommentBase> commentList) {
        this.id = id;
        this.useId = useId;
        this.decStr = decStr;
        this.friendImageId = friendImageId;
        this.time = time;
        this.friendVideoId = friendVideoId;
        this.friendVideoTime = friendVideoTime;
        this.likesId = likesId;
        this.commentList = commentList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUseId() {
        return useId;
    }

    public void setUseId(int useId) {
        this.useId = useId;
    }

    public String getDecStr() {
        return decStr;
    }

    public void setDecStr(String decStr) {
        this.decStr = decStr;
    }

    public String getFriendImageId() {
        return friendImageId;
    }

    public void setFriendImageId(String friendImageId) {
        this.friendImageId = friendImageId;
    }

    public String getFriendVideoId() {
        return friendVideoId;
    }

    public void setFriendVideoId(String friendVideoId) {
        this.friendVideoId = friendVideoId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFriendVideoTime() {
        return friendVideoTime;
    }

    public String getLikesId() {
        return likesId;
    }

    public void setLikesId(String likesId) {
        this.likesId = likesId;
    }

    public void setFriendVideoTime(String friendVideoTime) {
        this.friendVideoTime = friendVideoTime;
    }

    public List<CommentBase> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentBase> commentList) {
        this.commentList = commentList;
    }
}
