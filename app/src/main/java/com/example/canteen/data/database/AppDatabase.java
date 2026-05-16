package com.example.canteen.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import androidx.room.TypeConverters;

import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.canteen.data.dao.CommentDao;
import com.example.canteen.data.dao.FoodDao;
import com.example.canteen.data.dao.PostDao;
import com.example.canteen.data.entity.Comment;
import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.FoodPostCrossRef;
import com.example.canteen.data.entity.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room 数据库单例
 *
 * entities：列出所有实体类，version 升级时记得提供 Migration 或 fallbackToDestructiveMigration
 * exportSchema = false：不导出 schema JSON（生产项目建议 true 并纳入版本管理）
 */
@Database(
    entities  = { Food.class, Post.class, Comment.class, FoodPostCrossRef.class },
    version   = 2,
    exportSchema = false
)
@TypeConverters({Converters.class})  // 这里注册！
public abstract class AppDatabase extends RoomDatabase {

    // ── DAO 抽象方法 ──────────────────────────────────────
    public abstract FoodDao    foodDao();
    public abstract PostDao    postDao();
    public abstract CommentDao commentDao();

    //public abstract FoodPostCrossRefDao foodPostCrossRefDao();

    // ── 单例 ──────────────────────────────────────────────
    private static volatile AppDatabase INSTANCE;

    /**
     * 提供一个固定大小的线程池供 Repository 在后台执行数据库操作
     * （Room 不允许在主线程直接执行写操作）
     */
    public static final ExecutorService DB_EXECUTOR =
        Executors.newFixedThreadPool(4);

    /**
     * 获取数据库单例
     * @param context Application Context，避免内存泄漏
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "canteen_db"
                        )
                        // 首次创建数据库时注入示例数据
                        .addCallback(sRoomDatabaseCallback)
                        // 开发阶段允许破坏性迁移（升级 version 时清空重建）
                        // 生产阶段请改为 .addMigrations(MIGRATION_1_2, ...)
                        .fallbackToDestructiveMigration()
                        .build();
                }
            }
        }
        return INSTANCE;
    }

    // ── 数据库创建回调：注入示例数据 ─────────────────────
    private static final RoomDatabase.Callback sRoomDatabaseCallback =
        new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                // 在后台线程插入示例数据
                DB_EXECUTOR.execute(() -> {
                    System.out.println("数据库首次创建，正在插入示例数据...");
                    FoodDao foodDao = INSTANCE.foodDao();
                    PostDao postDao = INSTANCE.postDao();

                    List<Food> sampleFoods = buildSampleFoods();
                    foodDao.insert(sampleFoods);

                    // 给第一条食品添加一条示例帖子
                    // 注意：这里直接使用 postDao 插入帖子，并没有关联食品。实际使用中请先插入帖子，再插入关联表 FoodPostCrossRef 来建立关系。
                    //这一条没有插入成功，可能是因为 FoodPostCrossRef 表没有正确关联 Food 和 Post 导致的。建议先插入 Post，再插入 FoodPostCrossRef 来建立关系。
                    Post p = new Post("小明", "东区一食堂红烧肉太好吃了！",
                        "今天午饭去一食堂吃了红烧肉套餐，肉烂入味，推荐大家去试试！");
                    postDao.insert(p);


                    System.out.println("数据库已创建，示例数据已插入");
                });
            }
        };

    /** 构建示例食品数据 */
    private static List<Food> buildSampleFoods() {
        List<Food> list = new ArrayList<>();

        list.add(new Food(
            "东校区", "第一食堂", "1楼", "A01 家常档",
            "红烧肉套餐",
            "精选五花肉，慢火炖制，色泽红亮，入口即化。含米饭+一荤两素。",
            12.0, "11:00-13:30", new ArrayList<>(Arrays.asList("套餐","热食","猪肉","荤菜"))
        ));

        list.add(new Food(
            "东校区", "第一食堂", "1楼", "A02 面档",
            "招牌牛肉面",
            "浓郁骨汤，手切牛腱子，面条劲道爽滑。",
            10.0, "07:00-09:00,11:00-13:30", new ArrayList<>(Arrays.asList("面食","牛肉","早餐","午餐"))
        ));

        list.add(new Food(
            "东校区", "第二食堂", "2楼", "B05 特色档",
            "麻辣香锅",
            "自选食材，按重量计价，麻辣鲜香，可调辣度。",
            15.0, "11:00-14:00,17:00-20:00", new ArrayList<>(Arrays.asList("麻辣","自选","热食","荤素"))
        ));

        list.add(new Food(
            "西校区", "桂园食堂", "1楼", "C01 早餐档",
            "豆浆油条套餐",
            "现磨豆浆配现炸油条，营养健康的传统早餐。",
            5.0, "07:00-09:30", new ArrayList<>(Arrays.asList("早餐","豆浆","油条","素食"))
        ));

        list.add(new Food(
            "西校区", "桂园食堂", "2楼", "C08 盖饭档",
            "番茄鸡蛋盖饭",
            "经典家常番茄炒蛋，酸甜开胃，配软糯米饭。",
            8.0, "11:00-13:30,17:00-19:30", new ArrayList<>(Arrays.asList("盖饭","番茄","鸡蛋","素食"))
        ));

        list.add(new Food(
            "东校区", "第一食堂", "2楼", "D03 煲仔饭",
            "腊肠煲仔饭",
            "广式腊肠与软糯米饭的经典搭配，底部焦香饭焦最为惊喜。",
            14.0, "11:30-14:00", new ArrayList<>(Arrays.asList("煲仔饭","腊肠","热食","荤菜"))
        ));

        System.out.println("构建了 " + list.size() + " 条示例食品数据");
        return list;
    }
}
