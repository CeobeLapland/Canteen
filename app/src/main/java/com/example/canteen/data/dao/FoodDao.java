package com.example.canteen.data.dao;

import androidx.annotation.Nullable;
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
import com.example.canteen.data.entity.mid.FoodPostCrossRef;
import com.example.canteen.data.entity.mid.FoodTagCrossRef;
import com.example.canteen.data.entity.mid.FoodWithPosts;
import com.example.canteen.data.entity.mid.FoodWithTags;

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
    Single<Food> getFoodById(long id);

    @Query("SELECT * FROM foods ORDER BY name ASC LIMIT :pageSize OFFSET :pageNumber *:pageSize")
    Single<List<Food>> getFoodsByPage(int pageSize, int pageNumber);


    // 按标签匹配，一共需要needed个结果，优先返回匹配标签数多的食物，标签数相同则按名字排序
    // 还需要子查询，因为给的是string列表，不能直接用IN匹配，需要先在中间表里统计每个food_id匹配的标签数，再按这个数排序
    @Query("SELECT * FROM foods " +
            "WHERE id IN ( " +
            "    SELECT food_id FROM food_tag_cross_ref " +
            "    WHERE tag_id IN (SELECT id FROM tags WHERE name IN (:tags)) " +
            "    GROUP BY food_id " +
            "    ORDER BY COUNT(tag_id) DESC " +
            ") " +
            "ORDER BY name ASC " +
            "LIMIT :neededCount")
    Single<List<Food>> getFoodsByTags(List<String> tags, int neededCount);

    // 获取食物总数，用于分页计算
    @Query("SELECT COUNT(*) FROM foods")
    Single<Integer> getFoodCount();

    // 按名字模糊搜索
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    Single<List<Food>> searchFoodsByName(String keyword);

    @Query("SELECT * FROM foods " +
            "WHERE (:campus IS NULL OR campus = :campus) " +
            "AND (:canteen IS NULL OR canteen = :canteen) " +
            "AND (:floor IS NULL OR floor = :floor) " +
            "AND (:windowSell IS NULL OR window_sell = :windowSell) " +
            "AND (:nameKeyword IS NULL OR name LIKE '%' || :nameKeyword || '%') " +
            "ORDER BY name ASC " +
            "LIMIT :pageSize OFFSET :pageNumber *:pageSize")
    Single<List<Food>> getFoodsByDetailsPaged(
            @Nullable String campus, @Nullable String canteen,
            @Nullable String floor, @Nullable String windowSell,
            @Nullable String nameKeyword,
            int pageSize, int pageNumber
    );


    @RawQuery(observedEntities = Food.class)
    Single<List<Food>> getFoodsByCustomQuery(SupportSQLiteQuery query);




    // ---- 关联查询（Food <-> Post 多对多） ----
    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    Single<FoodWithPosts> getFoodWithPosts(long foodId);

    //这个函数好像用不上了，先注释掉
    //@Transaction
    //@Query("SELECT * FROM foods ORDER BY name ASC")
    //Single<List<FoodWithPosts>> getAllFoodsWithPosts();

    // 操作中间表
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFoodPostCrossRef(FoodPostCrossRef crossRef);

    @Delete
    void deleteFoodPostCrossRef(FoodPostCrossRef crossRef);


    // 关联查询 Food和Tag
    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    Single<FoodWithTags> getFoodWithTags(int foodId);

    //@Transaction
    //@Query("SELECT * FROM foods ORDER BY name ASC")
    //Single<List<FoodWithTags>> getAllFoodsWithTags();

    // 操作中间表（Food <-> Tag 多对多）
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFoodTagCrossRef(FoodTagCrossRef crossRef);

    @Delete
    void deleteFoodTagCrossRef(FoodTagCrossRef crossRef);

}