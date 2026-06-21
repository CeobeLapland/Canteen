package com.example.canteen.data.repository;

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