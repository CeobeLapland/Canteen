package com.example.canteen.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.canteen.data.dao.FoodDao;
import com.example.canteen.data.database.AppDatabase;
import com.example.canteen.data.entity.Food;

import java.util.ArrayList;
import java.util.List;




import com.example.canteen.net.manager.NetworkManager;
import com.example.canteen.net.api.FoodApi;

import com.example.canteen.net.dto.Dtos.FoodDetailDto;
import com.example.canteen.net.dto.Dtos.FoodSummaryDto;
import com.example.canteen.net.request.Requests.CreateFoodRequest;
import com.example.canteen.net.response.ApiResponse;
import com.example.canteen.net.response.PageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 食品 Repository
 * Repository 是 ViewModel 与数据源（Room / Network）之间的中间层：
 *  - ViewModel 只依赖 Repository，不感知底层是数据库还是网络
 *  - Repository 负责在正确的线程上执行操作
 */
public class FoodRepository {

    private final FoodDao foodDao;
    private final FoodApi api;

    public FoodRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        foodDao = db.foodDao();

        // 测试：打印所有食品名称，验证数据库连接
        AppDatabase.DB_EXECUTOR.execute(() -> {
            List<Food> foods = foodDao.getAllFoods().getValue();
            if (foods != null) {
                for (Food food : foods) {
                    System.out.println("FoodRepository Food: " + food.getName());
                }
            } else {
                System.out.println("FoodRepository No foods found in database.");
            }
        });
        //Retrofit retrofit = ApiClient.getRetrofit();
        //api = retrofit.create(FoodApi.class);
        api = NetworkManager.getInstance().create(FoodApi.class);
    }

    //region 操纵本地数据库（Room）
    // ── 读取（主线程安全，Room 自动切线程） ──────────────
    public LiveData<List<Food>> getAllFoods() {
            return foodDao.getAllFoods();
        /*//return foodDao.getAllFoods();
        System.out.println("FoodRepository getAllFoods called");
        //List<Food> foods = foodDao.getAllFoods().getValue();
        List<Food> foods = foodDao.getAllFoods().observe(, foodsList -> {
            if (foodsList != null) {
                for (Food food : foodsList) {
                    System.out.println("FoodRepository observed Food: " + food.getName());
                }
            } else {
                System.out.println("FoodRepository observed no foods in database.");
            }
        }).getValue();
        //if(foods == null) {
        //    System.out.println("FoodRepository getAllFoods: No foods found in database.");
            //foods = new ArrayList<>(); // 避免空指针，返回空列表
        //}
        for (Food food : foods) {
            System.out.println("FoodRepository getAllFoods: " + food.getName());
        }
        return new MutableLiveData<>(foods);*/
    }

    public LiveData<Food> getFoodById(int id) {
        return foodDao.getFoodById(id);
    }

    public LiveData<List<Food>> getFoodsByCampus(String campus) {
        return foodDao.getFoodsByCampus(campus);
    }

    //public LiveData<List<Food>> getFoodsByCanteen(String campus, String canteen) {
    //    return foodDao.getFoodsByCanteen(campus, canteen);
    //}

    /** 按 tag 搜索，自动拼接通配符 */
    //public LiveData<List<Food>> getFoodsByTag(String tag) {
        //return foodDao.getFoodsByTag("%" + tag + "%");
    //}

    /** 关键词搜索（名称/描述/标签） */
    public LiveData<List<Food>> searchFoods(String keyword) {
        //return foodDao.searchFoods("%" + keyword + "%");
        return foodDao.getAllFoods();
    }

    //public LiveData<List<String>> getAllCampuses() {
    //    return foodDao.getAllCampuses();
    //}

    //public LiveData<List<String>> getCanteensByCampus(String campus) {
        //return foodDao.getCanteensByCampus(campus);
    //}

    // ── 写入（必须在后台线程执行） ────────────────────────
    public void insert(Food food) {
        AppDatabase.DB_EXECUTOR.execute(() ->
                foodDao.insert(food));
    }

    public void insert(List<Food> foods) {
        AppDatabase.DB_EXECUTOR.execute(() ->
                foodDao.insert(foods));
    }

    public void update(Food food) {
        AppDatabase.DB_EXECUTOR.execute(() ->
                foodDao.update(food));
    }

    public void delete(Food food) {
        AppDatabase.DB_EXECUTOR.execute(() ->
                foodDao.delete(food));
    }

    /** 给食品添加一条评分 */
    public void addRating(int foodId, float rating) {
        //AppDatabase.DB_EXECUTOR.execute(() ->
                //foodDao.addRating(foodId, rating));
    }


    private final int PAGE_SIZE = 20; // 每页20条
    /**
     * 加载分页数据
     * 【替换成你自己的接口/数据库查询】
     */
    public MutableLiveData<List<Food>> loadFoodData(int page) {
        // ============== 这里替换成你的真实数据请求 ==============
        // 模拟网络请求（子线程）
        /*new Thread(() -> {
            // 模拟生成分页数据
            List<Food> data = new ArrayList<>();
            int start = (page - 1) * PAGE_SIZE;
            int end = start + PAGE_SIZE;
            for (int i = start; i < end; i++) {
                if (i > 200) break; // 模拟总共200条数据
                Food food = new Food();
                food.setId(i);
                food.setName("食品" + i);
                food.setFullLocation("位置" + i);
                food.setPrice(99.0f);
                food.setTags(List.of("标签1", "标签2"));
                food.setAverageRating(4.5f);
                food.setRatingCount(100);
                data.add(food);
            }

            // 切回主线程更新列表
            runOnUiThread(() -> {
                if (page == 1) {
                    adapter.setNewList(data); // 第一页：初始化
                } else {
                    adapter.addMoreList(data); // 下一页：追加
                }
            });
        }).start();*/
        return new MutableLiveData<>(); // 占位，实际应返回分页数据
    }

    public List<Food> loadPage(int page) {
        //return new ArrayList<>(); // 占位，实际应返回分页数据
        return foodDao.getAllFoods().getValue(); // 注意：这里直接返回全部数据，实际应实现分页查询
    }




    //endregion


    // region 原始 Retrofit 调用（保留原始 Callback 版本，供需要直接使用 Retrofit 的场景）
    /**
     * 网络：全量拉取所有菜品（不分页）
     */
    public void fetchAllFoodsNoPagination(Callback<ApiResponse<List<FoodDetailDto>>> callback) {
        Call<ApiResponse<List<FoodDetailDto>>> call = api.getAllFoodsNoPagination();
        call.enqueue(callback);
    }

    /** 增量更新：获取自指定时间之后新增/更新的菜品列表 */
    public void fetchUpdatedFoods(String since, Callback<ApiResponse<List<FoodDetailDto>>> callback) {
        Call<ApiResponse<List<FoodDetailDto>>> call = api.getUpdatedFoods(since);
        call.enqueue(callback);
    }

    /** 分页：获取所有菜品（分页） */
    public void fetchAllFoodsPaged(int page, int size, Callback<ApiResponse<PageResponse<FoodSummaryDto>>> callback) {
        Call<ApiResponse<PageResponse<FoodSummaryDto>>> call = api.getAllFoods(page, size);
        call.enqueue(callback);
    }

    /** 搜索（分页） */
    public void searchFoodsRemote(String keyword, int page, int size, Callback<ApiResponse<PageResponse<FoodSummaryDto>>> callback) {
        Call<ApiResponse<PageResponse<FoodSummaryDto>>> call = api.searchFoods(keyword, page, size);
        call.enqueue(callback);
    }

    /** 获取菜品详情 */
    public void fetchFoodById(long id, Callback<ApiResponse<FoodDetailDto>> callback) {
        Call<ApiResponse<FoodDetailDto>> call = api.getFoodById(id);
        call.enqueue(callback);
    }

    /** 新增菜品 */
    public void createFoodRemote(CreateFoodRequest request, Callback<ApiResponse<FoodDetailDto>> callback) {
        Call<ApiResponse<FoodDetailDto>> call = api.createFood(request);
        call.enqueue(callback);
    }

    /** 更新菜品 */
    public void updateFoodRemote(long id, CreateFoodRequest request, Callback<ApiResponse<FoodDetailDto>> callback) {
        Call<ApiResponse<FoodDetailDto>> call = api.updateFood(id, request);
        call.enqueue(callback);
    }

    /** 删除菜品 */
    public void deleteFoodRemote(long id, Callback<ApiResponse<Void>> callback) {
        Call<ApiResponse<Void>> call = api.deleteFood(id);
        call.enqueue(callback);
    }
    // endregion

    //region 操纵远程服务器（Retrofit）
    /**
     * 统一的仓库回调封装：把后端的 ApiResponse 映射为 onSuccess/onError/onFailure
     */
    public interface RepositoryCallback<T> {
        /** Called when request and ApiResponse indicate success. */
        void onSuccess(T data);

        /** Called when server returns a business error (ApiResponse.success == false) or HTTP error. */
        void onError(int code, String message);

        /** Called when network/serialization failure occurs. */
        void onFailure(Throwable t);
    }

    /** 内部通用方法：把 Call<ApiResponse<T>> 转换为 RepositoryCallback<T> */
    private <T> void enqueueCall(Call<ApiResponse<T>> call, RepositoryCallback<T> callback) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<T> apiResp = response.body();
                    if (apiResp != null) {
                        if (apiResp.isSuccess()) {
                            callback.onSuccess(apiResp.getData());
                        } else {
                            callback.onError(apiResp.getCode(), apiResp.getMessage());
                        }
                    } else {
                        // HTTP 2xx but empty body
                        callback.onError(response.code(), "Empty response body");
                    }
                } else {
                    // HTTP error (4xx/5xx)
                    callback.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // 同时提供基于 RepositoryCallback 的更友好的包装方法（保持原有 raw Callback 方法兼容）
    public void fetchAllFoodsNoPaginationAsync(RepositoryCallback<List<FoodDetailDto>> callback) {
        enqueueCall(api.getAllFoodsNoPagination(), callback);
    }

    public void fetchUpdatedFoodsAsync(String since, RepositoryCallback<List<FoodDetailDto>> callback) {
        enqueueCall(api.getUpdatedFoods(since), callback);
    }

    public void fetchAllFoodsPagedAsync(int page, int size, RepositoryCallback<PageResponse<FoodSummaryDto>> callback) {
        enqueueCall(api.getAllFoods(page, size), callback);
    }

    public void searchFoodsRemoteAsync(String keyword, int page, int size, RepositoryCallback<PageResponse<FoodSummaryDto>> callback) {
        enqueueCall(api.searchFoods(keyword, page, size), callback);
    }

    public void fetchFoodByIdAsync(long id, RepositoryCallback<FoodDetailDto> callback) {
        enqueueCall(api.getFoodById(id), callback);
    }

    public void createFoodRemoteAsync(CreateFoodRequest request, RepositoryCallback<FoodDetailDto> callback) {
        enqueueCall(api.createFood(request), callback);
    }

    public void updateFoodRemoteAsync(long id, CreateFoodRequest request, RepositoryCallback<FoodDetailDto> callback) {
        enqueueCall(api.updateFood(id, request), callback);
    }

    public void deleteFoodRemoteAsync(long id, RepositoryCallback<Void> callback) {
        enqueueCall(api.deleteFood(id), callback);
    }

    //endregion
}