package com.example.canteen.data.repository;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.canteen.data.dao.CommentDao;
import com.example.canteen.data.dao.FoodDao;
import com.example.canteen.data.dao.PostDao;
import com.example.canteen.data.database.AppDatabase;
import com.example.canteen.data.entity.Comment;
import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.Post;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 帖子 + 评论 Repository
 * 评论的增删需要同时维护 posts.comment_count，
 * 因此将两者放在同一 Repository 中方便协调。
 */
public class PostRepository {

    private static volatile PostRepository instance;

    public static PostRepository getInstance() {
        /*if (instance == null) {
            synchronized (PostRepository.class) {
                if (instance == null) {
                    instance = new PostRepository(application);
                }
            }
        }*/
        return instance;
    }

    private final PostDao    postDao;
    private final FoodDao   foodDao;//先凑合用一下
    private final CommentDao commentDao;


    private static final int PAGE_SIZE = 20; // 每页加载的帖子数量

    public PostRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        postDao    = db.postDao();
        commentDao = db.commentDao();

        foodDao   = db.foodDao();
    }

    // ── 帖子：读取 ────────────────────────────────────────
    public Single<List<Post>> getAllPosts() {
        return postDao.getAllPosts().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Post> getPostById(long id) {
        // 返回一个示例帖子，实际应该从数据库查询
        return Single.just(new Post("作者喵", "找不到", "猜猜都有什么。"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        //return postDao.getPostById(id).
        //        subscribeOn(Schedulers.io()).
        //        observeOn(AndroidSchedulers.mainThread());
    }


    public Single<List<Post>> loadPageByTimeAsc(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return postDao.getPostsByTimeAsc(PAGE_SIZE, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    public Single<List<Post>> loadChannelPage(
            int page,
            int pageSize,
            String keyword,
            String typeFilter,
            String sortMode,
            boolean ascending
    ) {
        // 这里按你的 DAO / SQL 自己实现：
        // 1. keyword 为空时不做关键词过滤
        // 2. typeFilter = "全部" 时不做类型过滤
        // 3. sortMode = TIME / VIEW / LIKE
        // 4. ascending 控制升序降序
        //throw new UnsupportedOperationException("Implement me");
        //返回一组示例数据，实际应该从数据库查询
        return Single.just(List.of(
                new Post("Tom", "示例帖子1", "这是一个关于食物的帖子。"),
                new Post("Jerry", "示例帖子2", "这是另一个关于食物的帖子。")
        ));//.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Long> countChannelPosts(String keyword, String typeFilter) {
        // 返回当前筛选条件下的总数，用于分页按钮
        // 返回2
        return Single.just(2L)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        //throw new UnsupportedOperationException("Implement me");
    }

    // 放个占位
    public Single<List<Post>> getRelatedPosts(long postId) {
        // 这里可以按你的业务逻辑实现，比如根据帖子内容相似度、相同食物等
        // 先返回空
        return Single.just(List.of(
                new Post("Alice", "相关帖子1", "这是一个与当前帖子相关的帖子。"),
                new Post("Bob", "相关帖子2", "这是另一个与当前帖子相关的帖子。")
        ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        //throw new UnsupportedOperationException("Implement me");
    }

    public Single<Boolean> publishPost(Post post, List<String> types, @Nullable List<Long> foodIds) {
        // 这里需要在事务中同时插入 Post 和 Post-Food 关联表
        // 先返回成功
        return Single.fromCallable(() -> {
            //long postId = postDao.insert(post);
            // 插入关联表，假设有 postFoodDao.insert(postId, foodId)
            // for (Long foodId : foodIds) {
            //     postFoodDao.insert(postId, foodId);
            // }
            return true;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        //throw new UnsupportedOperationException("Implement me");
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
    //public LiveData<FoodWithPosts> getPostsByFood(int foodId) {
    //    return foodDao.getFoodWithPosts(foodId);
    //}

    // ── 帖子：写入 ────────────────────────────────────────
    public void insertPost(Post post) {
        AppDatabase.DB_EXECUTOR.execute(() -> postDao.insert(post));
    }

    public void deletePost(Post post) {
        // Room 外键 CASCADE 会自动删除该帖子下的所有评论
        AppDatabase.DB_EXECUTOR.execute(() -> postDao.delete(post));
    }

    //public void deletePostById(int postId) {
    //    AppDatabase.DB_EXECUTOR.execute(() -> postDao.deleteById(postId));
    //}

    ///** 帖子点赞（+1） */
    //public void likePost(int postId) {
    //    AppDatabase.DB_EXECUTOR.execute(() -> postDao.likePost(postId));
    //}

    // ── 评论：读取 ────────────────────────────────────────
    public LiveData<List<Comment>> getCommentsByPost(int postId) {
        return commentDao.getCommentsByPost(postId);
    }

    // ── 评论：写入（同时维护帖子评论计数） ───────────────
    public void insertComment(Comment comment) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            commentDao.insert(comment);
            //postDao.incrementCommentCount(comment.getPostId());
        });
    }

    public void deleteComment(Comment comment) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            commentDao.delete(comment);
            //postDao.decrementCommentCount(comment.getPostId());
        });
    }
}
