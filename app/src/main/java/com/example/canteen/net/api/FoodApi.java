package com.example.canteen.net.api;

import com.example.canteen.net.dto.Dtos.FoodDetailDto;
import com.example.canteen.net.dto.Dtos.FoodSummaryDto;
import com.example.canteen.net.request.Requests.CreateFoodRequest;
import com.example.canteen.net.response.ApiResponse;
import com.example.canteen.net.response.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface FoodApi {
	//得去掉前面的/，不然会被当成绝对路径，导致baseUrl失效
	@GET("v1/foods/all")
	Call<ApiResponse<List<FoodDetailDto>>> getAllFoodsNoPagination();

	@GET("v1/foods/updated")
	Call<ApiResponse<List<FoodDetailDto>>> getUpdatedFoods(@Query("since") String since);

	@GET("v1/foods")
	Call<ApiResponse<PageResponse<FoodSummaryDto>>> getAllFoods(@Query("page") int page, @Query("size") int size);

	@GET("v1/foods/search")
	Call<ApiResponse<PageResponse<FoodSummaryDto>>> searchFoods(@Query("keyword") String keyword, @Query("page") int page, @Query("size") int size);

	@GET("v1/foods/{id}")
	Call<ApiResponse<FoodDetailDto>> getFoodById(@Path("id") long id);

	@POST("v1/foods")
	Call<ApiResponse<FoodDetailDto>> createFood(@Body CreateFoodRequest request);

	@PUT("v1/foods/{id}")
	Call<ApiResponse<FoodDetailDto>> updateFood(@Path("id") long id, @Body CreateFoodRequest request);

	@DELETE("v1/foods/{id}")
	Call<ApiResponse<Void>> deleteFood(@Path("id") long id);
}