package com.example.canteen.net.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Android 端统一 API 响应包装类
 * 对应后端 com.canteen.model.response.ApiResponse
 */
public class ApiResponse<T> implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    /**
     * 后端返回的时间格式化字符串（例如 "2025-01-01 12:00:00"）
     */
    @SerializedName("timestamp")
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

}