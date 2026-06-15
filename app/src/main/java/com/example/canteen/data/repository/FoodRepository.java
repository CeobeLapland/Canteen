package com.example.canteen.data.repository;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.RoomSQLiteQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;

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

// 正确 3.x 包
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 食品 Repository
 * Repository 是 ViewModel 与数据源（Room / Network）之间的中间层：
 * - ViewModel 只依赖 Repository，不感知底层是数据库还是网络
 * - Repository 负责在正确的线程上执行操作
 */
public class FoodRepository {

    // 改成单例模式吧，避免多次创建数据库实例和 Retrofit 实例
    public static volatile FoodRepository instance;

    private final FoodDao foodDao;
    private final FoodApi api;

    public FoodRepository(Application application) {
        if(instance != null) {
            throw new IllegalStateException("FoodRepository already initialized");
        }
        else {
            System.out.println("Initializing FoodRepository singleton instance");
            instance = this;
        }

        AppDatabase db = AppDatabase.getInstance(application);
        foodDao = db.foodDao();

        // 测试：打印所有食品名称，验证数据库连接
        AppDatabase.DB_EXECUTOR.execute(() -> {
            foodDao.getAllFoods()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(foods -> {
                        if (foods != null) {
                            for (Food food : foods) {
                                System.out.println("FoodRepository initialized with Food: " + food.getName());
                            }
                        } else {
                            System.out.println("FoodRepository initialized but no foods found in database.");
                        }
                    }, throwable -> {
                        System.err.println("Error observing foods in FoodRepository: " + throwable.getMessage());
                    });
        });

        api = NetworkManager.getInstance().create(FoodApi.class);
    }

    //region 操纵本地数据库（Room）
    public Single<List<Food>> getAllFoods() {
        return foodDao.getAllFoods()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Food> getFoodById(int id) {
        return foodDao.getFoodById(id);
    }



    private final int PAGE_SIZE = 20; // 每页20条


    public Single<List<Food>> loadPage(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return foodDao.getFoodsByPage(PAGE_SIZE, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


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

    /**
     * 给食品添加一条评分
     */
    public void addRating(int foodId, float rating) {
        //AppDatabase.DB_EXECUTOR.execute(() ->
        //foodDao.addRating(foodId, rating));
    }




    public Single<List<Food>> getFoodsByDetailsPaged(
            @Nullable String campus, @Nullable String canteen,
            @Nullable String floor, @Nullable String window,
            @Nullable String nameKeyword,
            int pageSize, int pageNumber
    ){
        int offset = (pageNumber - 1) * pageSize;
        return foodDao.getFoodsByDetailsPaged(campus, canteen, floor, window, nameKeyword, pageSize, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 自定义复杂查询
    // @RawQuery(observedEntities = Food.class)
    // Single<List<Food>> getFoodsByCustomQuery(String query, Object[] args);
    public Single<List<Food>> getFoodsByCustomQuery(
            String campus, String canteen, String floor, String window, String nameKeyword,
            int minPrice, int maxPrice,
            int pageSize, int pageNumber
    ) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM foods WHERE 1=1");
        List<Object> argsList = new ArrayList<>();

        if (campus != null) {
            queryBuilder.append(" AND campus = ?");
            argsList.add(campus);
        }
        if (canteen != null) {
            queryBuilder.append(" AND canteen = ?");
            argsList.add(canteen);
        }
        if (floor != null) {
            queryBuilder.append(" AND floor = ?");
            argsList.add(floor);
        }
        if (window != null) {
            queryBuilder.append(" AND window = ?");
            argsList.add(window);
        }
        if (nameKeyword != null) {
            queryBuilder.append(" AND name LIKE ?");
            argsList.add("%" + nameKeyword + "%");
        }
        if (minPrice >= 0) {
            queryBuilder.append(" AND price >= ?");
            argsList.add(minPrice);
        }
        if (maxPrice >= 0) {
            queryBuilder.append(" AND price <= ?");
            argsList.add(maxPrice);
        }

        // 分页
        int offset = (pageNumber - 1) * pageSize;
        queryBuilder.append(" ORDER BY name ASC LIMIT ? OFFSET ?");
        argsList.add(pageSize);
        argsList.add(offset);

        String finalQuery = queryBuilder.toString();
        Object[] finalArgs = argsList.toArray();

        return foodDao.getFoodsByCustomQuery(new SimpleSQLiteQuery(finalQuery, finalArgs))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    /**
     * 增量更新：获取自指定时间之后新增/更新的菜品列表
     */
    public void fetchUpdatedFoods(String since, Callback<ApiResponse<List<FoodDetailDto>>> callback) {
        Call<ApiResponse<List<FoodDetailDto>>> call = api.getUpdatedFoods(since);
        call.enqueue(callback);
    }

    /**
     * 分页：获取所有菜品（分页）
     */
    public void fetchAllFoodsPaged(int page, int size, Callback<ApiResponse<PageResponse<FoodSummaryDto>>> callback) {
        Call<ApiResponse<PageResponse<FoodSummaryDto>>> call = api.getAllFoods(page, size);
        call.enqueue(callback);
    }

    /**
     * 搜索（分页）
     */
    public void searchFoodsRemote(String keyword, int page, int size, Callback<ApiResponse<PageResponse<FoodSummaryDto>>> callback) {
        Call<ApiResponse<PageResponse<FoodSummaryDto>>> call = api.searchFoods(keyword, page, size);
        call.enqueue(callback);
    }

    /**
     * 获取菜品详情
     */
    public void fetchFoodById(long id, Callback<ApiResponse<FoodDetailDto>> callback) {
        Call<ApiResponse<FoodDetailDto>> call = api.getFoodById(id);
        call.enqueue(callback);
    }

    /**
     * 新增菜品
     */
    public void createFoodRemote(CreateFoodRequest request, Callback<ApiResponse<FoodDetailDto>> callback) {
        Call<ApiResponse<FoodDetailDto>> call = api.createFood(request);
        call.enqueue(callback);
    }

    /**
     * 更新菜品
     */
    public void updateFoodRemote(long id, CreateFoodRequest request, Callback<ApiResponse<FoodDetailDto>> callback) {
        Call<ApiResponse<FoodDetailDto>> call = api.updateFood(id, request);
        call.enqueue(callback);
    }

    /**
     * 删除菜品
     */
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
        /**
         * Called when request and ApiResponse indicate success.
         */
        void onSuccess(T data);

        /**
         * Called when server returns a business error (ApiResponse.success == false) or HTTP error.
         */
        void onError(int code, String message);

        /**
         * Called when network/serialization failure occurs.
         */
        void onFailure(Throwable t);
    }

    /**
     * 内部通用方法：把 Call<ApiResponse<T>> 转换为 RepositoryCallback<T>
     */
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