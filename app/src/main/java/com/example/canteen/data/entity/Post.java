package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import androidx.room.PrimaryKey;

/**帖子实体类*/
@Entity(tableName = "posts")
public class Post {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /** 发帖人昵称（MVP 阶段不做账号系统，直接存字符串） */
    @ColumnInfo(name = "author_name")
    private String authorName;

    /** 帖子标题 */
    @ColumnInfo(name = "title")
    private String title;

    /** 帖子正文内容 */
    @ColumnInfo(name = "content")
    private String content;

    /** 发帖时间（Unix 时间戳，毫秒） */
    @ColumnInfo(name = "created_at")
    private long createdAt;

    /** 点赞数 */
    @ColumnInfo(name = "like_count")
    private int likeCount;

    /** 评论数（冗余字段，避免每次 JOIN 统计） */
    @ColumnInfo(name = "comment_count")
    private int commentCount;

    // ── 构造函数 ──────────────────────────────────────────


    public Post(String authorName, String title, String content) {
        this.authorName       = authorName;
        this.title        = title;
        this.content      = content;
        this.createdAt    = System.currentTimeMillis();
        this.likeCount    = 0;
        this.commentCount = 0;
    }

    //region ── Getters & Setters ─────────────────────────────────
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getAuthorName()                   { return authorName; }
    public void setAuthorName(String authorName)   { this.authorName = authorName; }

    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }

    public String getContent()                  { return content; }
    public void setContent(String content)      { this.content = content; }

    public long getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(long createdAt)    { this.createdAt = createdAt; }

    public int getLikeCount()                   { return likeCount; }
    public void setLikeCount(int likeCount)     { this.likeCount = likeCount; }

    public int getCommentCount()                { return commentCount; }
    public void setCommentCount(int c)          { this.commentCount = c; }
    //endregion
}
