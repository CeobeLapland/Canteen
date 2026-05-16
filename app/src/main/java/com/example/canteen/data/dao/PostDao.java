package com.example.canteen.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import com.example.canteen.data.entity.Post;
import com.example.canteen.data.entity.PostWithFoods;
import com.example.canteen.data.entity.PostWithComments;
import com.example.canteen.data.entity.FoodPostCrossRef;

/**帖子数据访问对象（DAO）*/
@Dao
public interface PostDao {
	// ---- 基本增删改查 ----
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long insert(Post post);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	List<Long> insert(List<Post> posts);

	@Update
	void update(Post post);

	@Delete
	void delete(Post post);

	@Query("SELECT * FROM posts ORDER BY created_at DESC")
	LiveData<List<Post>> getAllPosts();

	@Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
	LiveData<Post> getPostById(int id);

	@Query("SELECT * FROM posts WHERE author_name = :author ORDER BY created_at DESC")
	LiveData<List<Post>> getPostsByAuthor(String author);

	// ---- 关联查询（Post <-> Food 多对多） ----
	@Transaction
	@Query("SELECT * FROM posts WHERE id = :postId")
	LiveData<PostWithFoods> getPostWithFoods(int postId);

	@Transaction
	@Query("SELECT * FROM posts ORDER BY created_at DESC")
	LiveData<List<PostWithFoods>> getAllPostsWithFoods();

	// ---- 关联查询（Post -> Comment 一对多） ----
	@Transaction
	@Query("SELECT * FROM posts WHERE id = :postId")
	LiveData<PostWithComments> getPostWithComments(int postId);

	@Transaction
	@Query("SELECT * FROM posts ORDER BY created_at DESC")
	LiveData<List<PostWithComments>> getAllPostsWithComments();

	// ---- 中间表操作 ----
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	void insertFoodPostCrossRef(FoodPostCrossRef crossRef);

	@Delete
	void deleteFoodPostCrossRef(FoodPostCrossRef crossRef);
}