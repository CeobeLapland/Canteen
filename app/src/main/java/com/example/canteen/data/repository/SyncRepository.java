package com.example.canteen.data.repository;

import android.app.Application;

import com.example.canteen.data.dao.CommentDao;
import com.example.canteen.data.dao.FoodDao;
import com.example.canteen.data.dao.PostDao;
import com.example.canteen.data.dao.SeasoningDao;
import com.example.canteen.data.dao.TagDao;
import com.example.canteen.data.dao.TypeDao;
import com.example.canteen.data.dao.WindowDao;
import com.example.canteen.data.database.AppDatabase;
import com.example.canteen.data.database.Converters;
import com.example.canteen.data.entity.Food;
import com.example.canteen.net.api.SyncApi;
import com.example.canteen.net.dto.SyncDto;
import com.example.canteen.net.dto.SyncDto.*;

import com.example.canteen.net.manager.NetworkManager;
import com.example.canteen.net.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncRepository {
    // 这个类是专门用于处理前端room数据库全量同步的Repository
    // 主要负责从后端获取全量数据，并提供给前端的room数据库进行同步
    // 这个类的实现会比较简单，主要是调用后端的API接口获取全量数据，然后把数据转换成前端room数据库需要的格式，并提供给前端进行同步。

    //单例
    private static SyncRepository instance;

    public static SyncRepository getInstance() {
        return instance;
    }

    private final SyncApi syncApi;

    // 下面是DTO的详细内容
    /*

public class SyncDto {
    // Food 实体的全量信息
    @Data
    public static class FoodSyncDto {
        private Long id;
        private String name;
        private String description;
        private Integer price;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private String campus;
        private String canteen;
        private String floor;
        // window存的是Id
        private Long windowId;

        private String sellTime;

        private Float averageRating;
        private Integer ratingCount;
    }


    // Tag 实体的全量信息
    @Data
    public static class TagSyncDto {
        private Long id;
        private String name;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


    // Window 实体的全量信息
    @Data
    public static class WindowSyncDto {
        private Long id;
        private String name;

        private String canteenName;
        private String campusName;
        private String floorName;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // Seasoning 实体的全量信息
    @Data
    public static class SeasoningSyncDto {
        private Long id;
        private String name;

        private Long windowId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // Post 实体的全量信息
    @Data
    public static class PostSyncDto {
        private Long id;
        private String title;
        private String content;

        private Integer viewCount;
        private Integer likeCount;

        private Long userId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // Type 实体的全量信息
    @Data
    public static class TypeSyncDto {
        private Long id;
        private String name;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // Comment 实体的全量信息
    @Data
    public static class CommentSyncDto {
        private Long id;
        private String content;

        private Long userId;
        private Long postId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


    // 多对多的中间表信息

    // Food-Tag 关联信息
    @Data
    public static class FoodTagSyncDto {
        private Long foodId;
        private Long tagId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // Food-Post 关联信息
    @Data
    public static class FoodPostSyncDto {
        private Long foodId;
        private Long postId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    //Post-Type 关联信息
    @Data
    public static class PostTypeSyncDto {
        private Long postId;
        private Long typeId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


    //整体Dto，即包含所有实体和关联信息的DTO，供前端全量同步使用
    @Data
    public static class AllSyncDto {
        //先把所有privite全改成public，后续再根据需要改回private并添加getter/setter
        public java.util.List<SyncDto.FoodSyncDto> foods;
        public java.util.List<SyncDto.TagSyncDto> tags;
        public java.util.List<SyncDto.WindowSyncDto> windows;
        public java.util.List<SyncDto.SeasoningSyncDto> seasonings;
        public java.util.List<SyncDto.PostSyncDto> posts;
        public java.util.List<SyncDto.TypeSyncDto> types;
        public java.util.List<SyncDto.CommentSyncDto> comments;

        public java.util.List<SyncDto.FoodTagSyncDto> foodTags;
        public java.util.List<SyncDto.FoodPostSyncDto> foodPosts;
        public java.util.List<SyncDto.PostTypeSyncDto> postTypes;
    }
}
     */
    // 先把所需要的dao列一下
    private final FoodDao foodDao;
    private final TagDao tagDao;
    private final WindowDao windowDao;
    private final SeasoningDao seasoningDao;
    private final PostDao postDao;
    private final TypeDao typeDao;
    private final CommentDao commentDao;

    // 这里存一下上次同步的时间，增量同步接口会用到
    private String lastSyncTime;

    public SyncRepository(Application application) {
        if (instance == null)
            instance = this;

        // 初始化DAO
        AppDatabase db = AppDatabase.getInstance(application);
        foodDao = db.foodDao();
        tagDao = db.tagDao();
        windowDao = db.windowDao();
        seasoningDao = db.seasoningDao();
        postDao = db.postDao();
        typeDao = db.typeDao();
        commentDao = db.commentDao();

        syncApi = NetworkManager.getInstance().create(SyncApi.class);

        // 初始化lastSyncTime，可以从SharedPreferences或者其他持久化存储中读取上次同步的时间，或者直接设置为null表示第一次全量同步
        //这里测试先设置成一个比较早的时间，后续再改成从持久化存储中读取
        lastSyncTime = "2024-01-01T00:00:00";
    }


    // 获取FoodDto并插入数据库
    public void UpdateFoodData(List<FoodSyncDto> foodSyncDtos, boolean isFullSync)
    {
        //分三种情况，如果传入数据为不为null，就直接插入；如果传入数据为null且是全量同步，就先清空数据库再插入；如果传入数据为null且是增量同步，就增量同步
        if(foodSyncDtos!=null) {//用Converters类把FoodSyncDto转换成Food实体类，然后插入数据库
            List<Food> foods = Converters.convertFoodList(foodSyncDtos);
            for (Food food : foods) {
                foodDao.insert(food);
            }
            return;
        }

        if(isFullSync)
        {
            //全量同步，从api中获取数据并插入数据库
            //foodDao.deleteAll();
            syncFoods(null, new RepositoryCallback<List<FoodSyncDto>>() {
                @Override
                public void onSuccess(List<FoodSyncDto> data) {
                    List<Food> foods = Converters.convertFoodList(data);
                    //foodDao.deleteAll();
                    for (Food food : foods) {
                        foodDao.insert(food);
                    }
                }

                @Override
                public void onError(int code, String message) {
                    // 处理错误，例如记录日志或通知用户
                }

                @Override
                public void onFailure(Throwable t) {
                    // 处理网络或序列化失败，例如记录日志或通知用户
                }
            });
        }
        else
        {
            //增量同步，从api中获取数据并插入数据库
            // since参数可以传入上次同步的时间，后端会根据这个时间返回自那时以来有更新的记录
            syncFoods(lastSyncTime, new RepositoryCallback<List<FoodSyncDto>>() {
                @Override
                public void onSuccess(List<FoodSyncDto> data) {
                    List<Food> foods = Converters.convertFoodList(data);
                    for (Food food : foods) {
                        foodDao.insert(food);
                    }
                }

                @Override
                public void onError(int code, String message) {
                    // 处理错误，例如记录日志或通知用户
                }

                @Override
                public void onFailure(Throwable t) {
                    // 处理网络或序列化失败，例如记录日志或通知用户
                }
            });
        }
    }

    // 其他实体的更新方法同理，这里就不写了，后续可以根据需要添加
    //现在开始写


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


    /** 测试接口：获取一个简单的字符串，前端可以用来测试网络请求是否正常 */
    public void testSync(RepositoryCallback<String> callback) {
        Call<ApiResponse<String>> call = syncApi.testSync();
        enqueueCall(call, callback);
    }

    //其实还有另一种写法，就是直接在每个接口方法里写enqueue的逻辑，这样就不需要单独写一个enqueueCall方法了，代码会更直接一些。不过为了代码复用和统一处理API响应，我觉得封装一个enqueueCall方法还是比较好的。
    // 接下来还是使用enqueueCall方法来实现全量同步和增量同步的接口吧，这样代码会更简洁一些。

    /** 全量同步接口：获取所有实体的全量信息，前端会根据这个DTO的结构来创建表和字段，并把数据插入到room数据库中。 */
    public void syncAllData(RepositoryCallback<SyncDto.AllSyncDto> callback) {
        enqueueCall(syncApi.syncAllData(), callback);
    }

    /** 增量同步接口：获取自since以来有更新的记录，前端会根据这些记录来更新room数据库中的对应数据。 */
    public void syncIncrementalData(String since, RepositoryCallback<SyncDto.AllSyncDto> callback) {
        enqueueCall(syncApi.syncIncrementalData(since), callback);
    }

     //下面是分开的增量同步接口，前端可以根据需要选择调用全量接口还是分开的增量接口（如果since参数为null或者空字符串，则表示全量同步）
    public void syncFoods(String since, RepositoryCallback<List<SyncDto.FoodSyncDto>> callback) {
        enqueueCall(syncApi.syncFoods(since), callback);
    }

    public void syncWindows(String since, RepositoryCallback<List<SyncDto.WindowSyncDto>> callback) {
        enqueueCall(syncApi.syncWindows(since), callback);
    }

    public void syncTags(String since, RepositoryCallback<List<SyncDto.TagSyncDto>> callback) {
        enqueueCall(syncApi.syncTags(since), callback);
    }

    public void syncPosts(String since, RepositoryCallback<List<SyncDto.PostSyncDto>> callback) {
        enqueueCall(syncApi.syncPosts(since), callback);
    }

    public void syncComments(String since, RepositoryCallback<List<SyncDto.CommentSyncDto>> callback) {
        enqueueCall(syncApi.syncComments(since), callback);
    }

    public void syncUsers(String since, RepositoryCallback<List<SyncDto.UserSyncDto>> callback) {
        enqueueCall(syncApi.syncUsers(since), callback);
    }

    public void syncSeasonings(String since, RepositoryCallback<List<SyncDto.SeasoningSyncDto>> callback) {
        enqueueCall(syncApi.syncSeasonings(since), callback);
    }

    public void syncTypes(String since, RepositoryCallback<List<SyncDto.TypeSyncDto>> callback) {
        enqueueCall(syncApi.syncTypes(since), callback);
    }
}
