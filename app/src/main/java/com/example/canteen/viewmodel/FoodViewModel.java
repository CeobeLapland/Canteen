package com.example.canteen.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.repository.FoodRepository;

import java.util.List;

/**
 * 食品列表 ViewModel
 * 继承 AndroidViewModel 以持有 Application Context（用于初始化 Repository）。
 * ViewModel 在配置变更（屏幕旋转）时存活，避免重复查询数据库。
 */
public class FoodViewModel extends AndroidViewModel
{

    private final FoodRepository repository;// = null;// 去掉了 final 修饰符，因为我们在构造函数中初始化它

    // ── 搜索/过滤状态（由 UI 驱动） ──────────────────────
    /** 当前搜索关键词，空字符串表示不过滤 */
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    /** 当前选中校区，null 表示全部 */
    private final MutableLiveData<String> selectedCampus = new MutableLiveData<>(null);

    // ── 对外暴露的 LiveData ───────────────────────────────
    /**
     * 根据 searchQuery 动态切换数据源：
     *  - 空串 → getAllFoods()
     *  - 非空 → searchFoods(keyword)
     * switchMap 会在 searchQuery 变化时自动切换底层 LiveData，
     * Activity/Fragment 只需 observe foodList，无需感知切换逻辑。
     */
    public final LiveData<List<Food>> foodList;

    /** 所有可选校区列表 */
    public final LiveData<List<String>> campusList;

    public FoodViewModel(@NonNull Application application)
    {
        super(application);
        repository = new FoodRepository(application);
        campusList = repository.getAllCampuses();

        foodList = Transformations.switchMap(searchQuery, query -> {

            if (query == null || query.trim().isEmpty()) {
                return repository.getAllFoods();
            } else {
                return repository.searchFoods(query.trim());
            }
        });
    }

    // ── UI 调用的操作方法 ─────────────────────────────────

    /** 更新搜索关键词（由搜索框文字变化触发） */
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    /** 按校区筛选（暂时直接更新 searchQuery 逻辑可扩展） */
    public void filterByCampus(String campus) {
        selectedCampus.setValue(campus);
        // TODO: 扩展 switchMap 联动多个过滤条件
    }

    /** 用户给某食品评分 */
    public void rateFood(int foodId, float rating) {
        repository.addRating(foodId, rating);
    }

    /** 管理员添加食品 */
    public void addFood(Food food) {
        repository.insert(food);
    }

    /** 获取单条食品详情（用于详情页） */
    public LiveData<Food> getFoodById(int foodId) {
        return repository.getFoodById(foodId);
    }
}
