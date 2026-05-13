package com.example.canteen.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.canteen.data.entity.Post;
import com.example.canteen.data.entity.PostWithComments;
import com.example.canteen.data.entity.PostWithFoods;

import java.util.List;

/**帖子数据访问对象（DAO）*/
@Dao
public interface PostDao {

    // ── 插入 ──────────────────────────────────────────────
    /** 插入帖子并返回自增主键 id */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Post post);

    // ── 更新 ──────────────────────────────────────────────
    @Update
    void update(Post post);

    // ── 删除 ──────────────────────────────────────────────
    @Delete
    void delete(Post post);

    @Query("DELETE FROM posts WHERE id = :postId")
    void deleteById(int postId);


    // ── 查询：全量 ────────────────────────────────────────
    /** 获取全部帖子，按发布时间倒序（最新在前） */
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    LiveData<List<Post>> getAllPosts();


    /** 获取热门帖子，按点赞数倒序（最多点赞在前） */
    @Query(" SELECT * FROM posts ORDER BY like_count DESC")
    LiveData<List<Post>> getHotPosts();

    /** 查询某作者的全部帖子，按发布时间倒序 */
    @Query("SELECT * FROM posts WHERE author_name = :authorName ORDER BY created_at DESC")
    LiveData<List<Post>> getPostsByAuthor(String authorName);

    /* ---------- 多对多：Post ↔ Food ---------- */
    @Transaction
    @Query("SELECT * FROM posts WHERE id = :postId")
    PostWithFoods getPostWithFoods(int postId);


    /* ---------- 一对多：Post ↔ Comment ---------- */
    @Transaction
    @Query("SELECT * FROM posts WHERE id = :postId")
    PostWithComments getPostWithComments(int postId);
    // ── 查询：单条 ────────────────────────────────────────
    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    LiveData<Post> getPostById(int postId);

    //查询某食品下的所有帖子(这个写在了FoodDao里)
    //@Query("SELECT p.* FROM posts p JOIN post_food_cross_ref pf ON p.id = pf.post_id WHERE pf.food_id = :foodId ORDER BY p.created_at DESC")
    //LiveData<List<Post>> getPostsByFoodId(int foodId);

    // ── 点赞（原子 +1） ───────────────────────────────────
    @Query("UPDATE posts SET like_count = like_count + 1 WHERE id = :postId")
    void likePost(int postId);

    // ── 更新评论数（新增评论后调用） ─────────────────────
    @Query("UPDATE posts SET comment_count = comment_count + 1 WHERE id = :postId")
    void incrementCommentCount(int postId);

    /** 评论被删除后调用，评论数 -1（不低于 0） */
    @Query("UPDATE posts SET comment_count = MAX(0, comment_count - 1) WHERE id = :postId")
    void decrementCommentCount(int postId);

}
