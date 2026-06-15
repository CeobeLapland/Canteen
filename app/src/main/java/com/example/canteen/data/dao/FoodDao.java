package com.example.canteen.data.dao;

import android.database.Observable;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import java.util.List;

import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.FoodPostCrossRef;
import com.example.canteen.data.entity.FoodWithPosts;

import io.reactivex.rxjava3.core.Single;

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
    Single<List<Food>> getAllFoods();

    @Query("SELECT * FROM foods WHERE id = :id LIMIT 1")
    Single<Food> getFoodById(int id);

    @Query("SELECT * FROM foods ORDER BY name ASC LIMIT :pageSize OFFSET :pageNumber *:pageSize")
    Single<List<Food>> getFoodsByPage(int pageSize, int pageNumber);



    @Query("SELECT * FROM foods " +
           "WHERE (:campus IS NULL OR campus = :campus) " +
             "AND (:canteen IS NULL OR canteen = :canteen) " +
             "AND (:floor IS NULL OR floor = :floor) " +
             "AND (:window IS NULL OR window = :window) " +
             "AND (:nameKeyword IS NULL OR name LIKE '%' || :nameKeyword || '%') " +
           "ORDER BY name ASC " +
           "LIMIT :pageSize OFFSET :pageNumber *:pageSize")
    Single<List<Food>> getFoodsByDetailsPaged(
            @Nullable String campus, @Nullable String canteen,
            @Nullable String floor,@Nullable String window,
            @Nullable String nameKeyword,
            int pageSize, int pageNumber
    );


    @RawQuery(observedEntities = Food.class)
    Single<List<Food>> getFoodsByCustomQuery(SupportSQLiteQuery query);


    // ---- 关联查询（Food <-> Post 多对多） ----
    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    Single<FoodWithPosts> getFoodWithPosts(int foodId);

    @Transaction
    @Query("SELECT * FROM foods ORDER BY name ASC")
    Single<List<FoodWithPosts>> getAllFoodsWithPosts();

    // 操作中间表
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFoodPostCrossRef(FoodPostCrossRef crossRef);

    @Delete
    void deleteFoodPostCrossRef(FoodPostCrossRef crossRef);
}