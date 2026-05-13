package com.example.canteen.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.FoodWithPosts;

import java.util.List;

import androidx.room.Transaction;

/**
 * 食品数据访问对象（DAO）
 * 所有返回 LiveData 的方法均在后台线程自动观察数据库变化；
 * 增删改操作需在 Repository 层通过 ExecutorService 切到子线程执行。
 */
@Dao
public interface FoodDao {

    // ── 插入 ──────────────────────────────────────────────
    /** 插入单条食品，主键冲突时替换 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Food food);

    /** 批量插入，用于初始化示例数据 */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Food> foods);

    // ── 更新 ──────────────────────────────────────────────
    @Update
    void update(Food food);

    // ── 删除 ──────────────────────────────────────────────
    @Delete
    void delete(Food food);

    // ── 查询：全量 ────────────────────────────────────────
    /** 获取所有食品，按评分降序排列；返回 LiveData，自动感知变化 */
    @Query("SELECT * FROM foods ORDER BY average_rating DESC")
    LiveData<List<Food>> getAllFoods();

    // ── 查询：单条 ────────────────────────────────────────
    @Query("SELECT * FROM foods WHERE id = :foodId LIMIT 1")
    LiveData<Food> getFoodById(int foodId);

    // ── 查询：按位置过滤 ──────────────────────────────────
    /** 按校区筛选 */
    @Query("SELECT * FROM foods WHERE campus = :campus ORDER BY average_rating DESC")
    LiveData<List<Food>> getFoodsByCampus(String campus);

    /** 按校区 + 食堂筛选 */
    @Query("SELECT * FROM foods WHERE campus = :campus AND canteen = :canteen ORDER BY floor")
    LiveData<List<Food>> getFoodsByCanteen(String campus, String canteen);

    // ── 查询：按标签过滤（LIKE 模糊匹配） ─────────────────
    /**
     * 查找包含指定 tag 的食品
     * 注意：传入参数需要调用方拼接通配符，例如 "%辣%"
     */
    @Query("SELECT * FROM foods WHERE tags LIKE :tagPattern ORDER BY average_rating DESC")
    LiveData<List<Food>> getFoodsByTag(String tagPattern);

    // ── 查询：关键词搜索 ──────────────────────────────────
    /** 在 name / description / tags 三个字段中模糊搜索 */
    @Query("SELECT * FROM foods WHERE name LIKE :keyword " +
           "OR description LIKE :keyword OR tags LIKE :keyword " +
           "ORDER BY average_rating DESC")
    LiveData<List<Food>> searchFoods(String keyword);

    // ── 更新评分（原子操作） ──────────────────────────────
    /**
     * 当用户给某食品评分时，重新计算加权平均分
     * newRating：本次新评分（1~5）
     */
    @Query("UPDATE foods SET " +
           "rating_count = rating_count + 1, " +
           "average_rating = (average_rating * rating_count + :newRating) / (rating_count + 1) " +
           "WHERE id = :foodId")
    void addRating(int foodId, float newRating);

    // ── 辅助：获取所有校区（去重） ───────────────────────
    @Query("SELECT DISTINCT campus FROM foods ORDER BY campus")
    LiveData<List<String>> getAllCampuses();

    /** 根据校区获取所有食堂（去重） */
    @Query("SELECT DISTINCT canteen FROM foods WHERE campus = :campus ORDER BY canteen")
    LiveData<List<String>> getCanteensByCampus(String campus);


    /* ---------- 多对多关系 ---------- */
    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    LiveData<FoodWithPosts> getFoodWithPosts(int foodId);
    //FoodWithPosts getFoodWithPosts(int foodId);
}