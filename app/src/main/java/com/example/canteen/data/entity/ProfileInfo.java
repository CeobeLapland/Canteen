package com.example.canteen.data.entity;


public class ProfileInfo {
    private String avatarUrl;       // 你后面可以换成真实头像地址/本地路径
    private String name;
    private long uid;
    private String joinTimeText;
    private String permission;

    private int postCount;
    private int viewCount;
    private int likeCount;
    private int commentCount;

    public ProfileInfo(String avatarUrl, String name, long uid, String joinTimeText, String permission,
                       int postCount, int viewCount, int likeCount, int commentCount) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.uid = uid;
        this.joinTimeText = joinTimeText;
        this.permission = permission;
        this.postCount = postCount;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public String getAvatarUrl() { return avatarUrl; }
    public String getName() { return name; }
    public long getUid() { return uid; }
    public String getJoinTimeText() { return joinTimeText; }
    public String getPermission() { return permission; }
    public int getPostCount() { return postCount; }
    public int getViewCount() { return viewCount; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
}
