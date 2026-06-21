package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**帖子实体类*/
@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "posts")
public class Post {

    @PrimaryKey(autoGenerate = true)
    private long id;

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
    private LocalDateTime createdAt;

    /** 点赞数 */
    @ColumnInfo(name = "like_count")
    private Integer likeCount;

    /** 观看数 */
    @ColumnInfo(name = "view_count")
    private Integer viewCount;

    /** 评论数（冗余字段，避免每次 JOIN 统计） */
    @ColumnInfo(name = "comment_count")
    private Integer commentCount;

    // ── 构造函数 ──────────────────────────────────────────
    @Ignore
    public Post(String authorName, String title, String content) {
        this.authorName       = authorName;
        this.title        = title;
        this.content      = content;
        this.createdAt    = LocalDateTime.now();
        this.likeCount    = 0;
        this.viewCount   = 0;
        this.commentCount = 0;
    }
}