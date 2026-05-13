package com.example.canteen.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.canteen.data.dao.CommentDao;
import com.example.canteen.data.dao.FoodDao;
import com.example.canteen.data.dao.PostDao;
import com.example.canteen.data.database.AppDatabase;
import com.example.canteen.data.entity.Comment;
import com.example.canteen.data.entity.FoodWithPosts;
import com.example.canteen.data.entity.Post;

import java.util.List;

/**
 * 帖子 + 评论 Repository
 * 评论的增删需要同时维护 posts.comment_count，
 * 因此将两者放在同一 Repository 中方便协调。
 */
public class PostRepository {

    private final PostDao    postDao;
    private final FoodDao   foodDao;//先凑合用一下
    private final CommentDao commentDao;

    public PostRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        postDao    = db.postDao();
        commentDao = db.commentDao();

        foodDao   = db.foodDao();
    }

    // ── 帖子：读取 ────────────────────────────────────────
    public LiveData<List<Post>> getAllPosts() {
        return postDao.getAllPosts();
    }

    public LiveData<Post> getPostById(int id) {
        return postDao.getPostById(id);
    }

    /*public LiveData<List<Post>> getPostsByFood(int foodId) {
        //return postDao.getPostsByFood(foodId);
        //return foodDao.getFoodWithPosts(foodId).map(foodWithPosts -> foodWithPosts.posts);
        //return foodDao.getFoodWithPosts(foodId).posts;
        //需要包装到 Transformations.map 中才能正确转换 LiveData<FoodWithPosts> 到 LiveData<List<Post>>
        return androidx.lifecycle.Transformations.map(foodDao.getFoodWithPosts(foodId), foodWithPosts -> {
            if (foodWithPosts == null) return null;
            return foodWithPosts.posts;
        });
    }*/
    public LiveData<FoodWithPosts> getPostsByFood(int foodId) {
        return foodDao.getFoodWithPosts(foodId);
    }

    // ── 帖子：写入 ────────────────────────────────────────
    public void insertPost(Post post) {
        AppDatabase.DB_EXECUTOR.execute(() -> postDao.insert(post));
    }

    public void deletePost(Post post) {
        // Room 外键 CASCADE 会自动删除该帖子下的所有评论
        AppDatabase.DB_EXECUTOR.execute(() -> postDao.delete(post));
    }

    public void deletePostById(int postId) {
        AppDatabase.DB_EXECUTOR.execute(() -> postDao.deleteById(postId));
    }

    /** 帖子点赞（+1） */
    public void likePost(int postId) {
        AppDatabase.DB_EXECUTOR.execute(() -> postDao.likePost(postId));
    }

    // ── 评论：读取 ────────────────────────────────────────
    public LiveData<List<Comment>> getCommentsByPost(int postId) {
        return commentDao.getCommentsByPost(postId);
    }

    // ── 评论：写入（同时维护帖子评论计数） ───────────────
    public void insertComment(Comment comment) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            commentDao.insert(comment);
            postDao.incrementCommentCount(comment.getPostId());
        });
    }

    public void deleteComment(Comment comment) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            commentDao.delete(comment);
            postDao.decrementCommentCount(comment.getPostId());
        });
    }
}
