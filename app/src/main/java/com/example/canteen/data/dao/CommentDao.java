package com.example.canteen.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.canteen.data.entity.Comment;

import java.util.List;

/**评论数据访问对象（DAO）*/
@Dao
public interface CommentDao {

    // ── 插入 ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Comment comment);

    // ── 删除 ──────────────────────────────────────────────
    @Delete
    void delete(Comment comment);

    @Query("DELETE FROM comments WHERE id = :commentId")
    void deleteById(int commentId);

    // ── 查询：某帖子的所有评论 ────────────────────────────
    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY created_at ASC")
    LiveData<List<Comment>> getCommentsByPost(int postId);

    // ── 查询：某帖子的评论数（非 LiveData，供事务内使用） ──
    @Query("SELECT COUNT(*) FROM comments WHERE post_id = :postId")
    int getCommentCount(int postId);

}