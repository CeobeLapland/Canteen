package com.example.canteen.data.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import androidx.room.Transaction;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.FoodPostCrossRef;
import com.example.canteen.data.entity.FoodWithPosts;

/**
 * 食品数据访问对象（DAO）
 * 所有返回 LiveData 的方法均在后台线程自动观察数据库变化；
 * 增删改操作需在 Repository 层通过 ExecutorService 切到子线程执行
 */
@Dao
public interface FoodDao {
    // ---- 基本增删改查 ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Food food);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<Food> foods);

    @Update
    void update(Food food);

    @Delete
    void delete(Food food);

    @Query("SELECT * FROM foods ORDER BY name ASC")
    LiveData<List<Food>> getAllFoods();

    @Query("SELECT * FROM foods WHERE id = :id LIMIT 1")
    LiveData<Food> getFoodById(int id);

    @Query("SELECT * FROM foods WHERE campus = :campus ORDER BY name ASC")
    LiveData<List<Food>> getFoodsByCampus(String campus);

    @Query("SELECT * FROM foods WHERE canteen = :canteen ORDER BY name ASC")
    LiveData<List<Food>> getFoodsByCanteen(String canteen);

    @Query("SELECT * FROM foods WHERE tags LIKE '%' || :tag || '%' ORDER BY name ASC")
    LiveData<List<Food>> getFoodsByTag(String tag);

    // ---- 关联查询（Food <-> Post 多对多） ----
    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    LiveData<FoodWithPosts> getFoodWithPosts(int foodId);

    @Transaction
    @Query("SELECT * FROM foods ORDER BY name ASC")
    LiveData<List<FoodWithPosts>> getAllFoodsWithPosts();

    // 操作中间表
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFoodPostCrossRef(FoodPostCrossRef crossRef);

    @Delete
    void deleteFoodPostCrossRef(FoodPostCrossRef crossRef);
}