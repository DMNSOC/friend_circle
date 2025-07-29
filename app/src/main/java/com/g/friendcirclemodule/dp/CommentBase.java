package com.g.friendcirclemodule.dp;

public class CommentBase {
    private int id;
    private int groupId;
    private final String commenter; // 评论者
    private final String replyTo;   // 被回复者
    private final String content;   // 评论内容

    public CommentBase(int id, int groupId, String commenter, String replyTo, String content) {
        this.id = id;
        this.groupId = groupId;
        this.commenter = commenter;
        this.replyTo = replyTo;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getContent() {
        return content;
    }
}