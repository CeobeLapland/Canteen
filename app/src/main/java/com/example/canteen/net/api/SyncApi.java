package com.example.canteen.net.api;

import com.example.canteen.net.dto.SyncDto;
import com.example.canteen.net.response.ApiResponse;

import java.util.List;

import retrofit2.http.GET;

public interface SyncApi
{

    /** 测试接口，返回一个简单的字符串，前端可以用来测试网络请求是否正常 */
    @GET("/v1/sync/test")
    retrofit2.Call<ApiResponse<String>> testSync();

    /**
     * 全量同步接口
     * <p>GET /api/v1/sync/all
     * <p>返回AllSyncDto中定义的所有实体的全量信息，前端会根据这个DTO的结构来创建表和字段，并把数据插入到room数据库中。
     * <p>注意：这个接口数据量较大，前端只有在应用更新或者用户手动触发全量同步时才会调用，平时请勿频繁调用。
     */
    @GET("/v1/sync/all")
    retrofit2.Call<ApiResponse<SyncDto.AllSyncDto>> syncAllData();


    /**
     * 增量同步接口
     * <p>GET /api/v1/sync/incremental?since=2024-06-01T00:00:00
     * <p>参数since是一个ISO格式的时间字符串，表示只同步这个时间点之后有更新的记录。前端会定期调用这个接口来获取最新的数据变化，以保持room数据库的实时性。
     * <p>返回结构同样是AllSyncDto，但里面只包含自since以来有更新的记录，前端会根据这些记录来更新room数据库中的对应数据。
     */
    @GET("/v1/sync/incremental")
    retrofit2.Call<ApiResponse<SyncDto.AllSyncDto>> syncIncrementalData(@retrofit2.http.Query("since") String since);

    //下面是分开的增量同步接口，前端可以根据需要选择调用全量接口还是分开的增量接口
    //从后端复制过来的，前端可以根据需要选择调用全量接口还是分开的增量接口（如果since参数为null或者空字符串，则表示全量同步）
    //现在把接口写一下

    @GET("v1/sync/foods")
    retrofit2.Call<ApiResponse<List<SyncDto.FoodSyncDto>>> syncFoods(@retrofit2.http.Query("since") String since);

    @GET("v1/sync/windows")
    retrofit2.Call<ApiResponse<List<SyncDto.WindowSyncDto>>> syncWindows(@retrofit2.http.Query("since") String since);

    @GET("v1/sync/tags")
    retrofit2.Call<ApiResponse<List<SyncDto.TagSyncDto>>> syncTags(@retrofit2.http.Query("since") String since);

    @GET("v1/sync/posts")
    retrofit2.Call<ApiResponse<List<SyncDto.PostSyncDto>>> syncPosts(@retrofit2.http.Query("since") String since);

    @GET("v1/sync/comments")
    retrofit2.Call<ApiResponse<List<SyncDto.CommentSyncDto>>> syncComments(@retrofit2.http.Query("since") String since);

    @GET("v1/sync/users")
    retrofit2.Call<ApiResponse<List<SyncDto.UserSyncDto>>> syncUsers(@retrofit2.http.Query("since") String since);

    @GET("v1/sync/seasonings")
    retrofit2.Call<ApiResponse<List<SyncDto.SeasoningSyncDto>>> syncSeasonings(@retrofit2.http.Query("since") String since);

    @GET("v1/sync/types")
    retrofit2.Call<ApiResponse<List<SyncDto.TypeSyncDto>>> syncTypes(@retrofit2.http.Query("since") String since);
}
