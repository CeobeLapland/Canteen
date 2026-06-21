package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**评论实体类*/
@Entity(
    tableName = "comments",
    foreignKeys = {
        @ForeignKey(
            entity = Post.class,
            parentColumns = "id",
            childColumns = "post_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Food.class,
            parentColumns = "id",
            childColumns = "food_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = { @Index("post_id"), @Index("food_id") } // 为外键字段创建索引，避免性能问题
)
@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @PrimaryKey(autoGenerate = true)
    private long id;



    // 增加了一个FoodId字段，表示该评论是针对哪个食品的，即现在评论要么针对帖子，要么针对食品
    //警告: food_id column references a foreign key but it is not part of an index. This may trigger full table scans whenever parent table is modified so you are highly advised to create an index that covers this column
    @ColumnInfo(name = "food_id")
    private Long foodId;  // 使用包装类 Long，允许为 null，表示该评论不针对任何食品，仅针对帖子

    /** 所属帖子 id */
    @ColumnInfo(name = "post_id")
    private Long postId;

    /** 评论者昵称 */
    @ColumnInfo(name = "author_name")
    private String authorName;

    /** 评论内容 */
    @ColumnInfo(name = "content")
    private String content;

    /** 点赞数 */
    @ColumnInfo(name = "like_count")
    private Integer likeCount;

    /** 评论时间（Unix 时间戳，毫秒） */
    @ColumnInfo(name = "created_at")
    private LocalDateTime createdAt;

    // ── 构造函数 ──────────────────────────────────────────
    //@Ignore
    //public Comment() {}

    @Ignore
    public Comment(Long postId, Long foodId, String authorName, String content) {

        this.postId       = postId;
        this.foodId       = foodId;
        this.authorName   = authorName;
        this.content      = content;
        this.createdAt    = LocalDateTime.now();
        this.likeCount = 0;
    }
}
