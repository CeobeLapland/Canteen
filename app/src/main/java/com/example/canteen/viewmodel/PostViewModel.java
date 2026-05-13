package com.example.canteen.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.canteen.data.entity.Comment;
import com.example.canteen.data.entity.FoodWithPosts;
import com.example.canteen.data.entity.Post;
import com.example.canteen.data.repository.PostRepository;

import java.util.List;

/**
 * 帖子 ViewModel
 *
 * 同时管理帖子列表和帖子详情（评论列表），
 * 通过 currentPostId 的 switchMap 联动评论列表。
 */
public class PostViewModel extends AndroidViewModel {

    private PostRepository repository;// 去掉了final修饰符，因为在构造函数中被赋值了

    /** 当前查看的帖子 id，由详情页设置 */
    private final MutableLiveData<Integer> currentPostId = new MutableLiveData<>();

    // ── 对外暴露 ──────────────────────────────────────────
    /** 全部帖子列表 */
    public final LiveData<List<Post>> allPosts;

    public PostViewModel(@NonNull Application application) {
        super(application);
        repository = new PostRepository(application);
        allPosts   = repository.getAllPosts();
    }

    /**
     * 当前帖子的评论列表
     * 随 currentPostId 变化自动切换
     */
    public final LiveData<List<Comment>> commentsOfCurrentPost =
        Transformations.switchMap(currentPostId, postId -> {
            if (postId == null) return new MutableLiveData<>(null);
            return repository.getCommentsByPost(postId);
        });

    /** 当前帖子详情 */
    public final LiveData<Post> currentPost =
        Transformations.switchMap(currentPostId, postId -> {
            if (postId == null) return new MutableLiveData<>(null);
            return repository.getPostById(postId);
        });


    // ── UI 操作方法 ───────────────────────────────────────

    /** 详情页打开时，通知 ViewModel 当前浏览的帖子 */
    public void selectPost(int postId) {
        currentPostId.setValue(postId);
    }

    /** 发布新帖子 */
    public void publishPost(String author, String title, String content) {
        Post post = new Post(author, title, content);
        repository.insertPost(post);
    }

    /** 删除帖子 */
    public void deletePost(Post post) {
        repository.deletePost(post);
    }

    /** 帖子点赞 */
    public void likePost(int postId) {
        repository.likePost(postId);
    }

    /** 发表评论 */
    public void addComment(int postId, String author, String content) {
        Comment comment = new Comment(postId, author, content);
        repository.insertComment(comment);
    }

    /** 删除评论 */
    public void deleteComment(Comment comment) {
        repository.deleteComment(comment);
    }

    /** 获取某食品的所有帖子（用于食品详情页内嵌帖子列表） */
    //public LiveData<List<Post>> getPostsByFood(int foodId) { return repository.getPostsByFood(foodId);}
    public LiveData<FoodWithPosts> getPostsByFood(int foodId) {
        return repository.getPostsByFood(foodId);
    }
}
