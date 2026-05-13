package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**评论实体类*/
@Entity(
    tableName = "comments",
    foreignKeys = @ForeignKey(
        entity        = Post.class,
        parentColumns = "id",
        childColumns  = "post_id",
        onDelete      = ForeignKey.CASCADE   // 帖子删除时，其下所有评论一并删除
    ),
    indices = { @Index("post_id") }
)
public class Comment {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /** 所属帖子 id */
    @ColumnInfo(name = "post_id")
    private int postId;

    /** 评论者昵称 */
    @ColumnInfo(name = "author_name")
    private String authorName;

    /** 评论内容 */
    @ColumnInfo(name = "content")
    private String content;

    /** 评论时间（Unix 时间戳，毫秒） */
    @ColumnInfo(name = "created_at")
    private long createdAt;

    // ── 构造函数 ──────────────────────────────────────────
    //@Ignore
    //public Comment() {}

    public Comment(int postId, String authorName, String content) {
        this.postId    = postId;
        this.authorName    = authorName;
        this.content   = content;
        this.createdAt = System.currentTimeMillis();
    }

    //region ── Getters & Setters ─────────────────────────────────
    public int getId()                       { return id; }
    public void setId(int id)               { this.id = id; }

    public int getPostId()                   { return postId; }
    public void setPostId(int postId)       { this.postId = postId; }

    public String getAuthorName()                { return authorName; }
    public void setAuthorName(String authorName)    { this.authorName = authorName; }

    public String getContent()               { return content; }
    public void setContent(String content)  { this.content = content; }

    public long getCreatedAt()               { return createdAt; }
    public void setCreatedAt(long t)        { this.createdAt = t; }
    //endregion
}
