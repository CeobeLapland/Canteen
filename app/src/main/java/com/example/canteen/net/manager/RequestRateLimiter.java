package com.example.canteen.net.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRateLimiter {

    // 接口 -> 上次请求时间
    private static final Map<String, Long> REQUEST_TIME_MAP = new ConcurrentHashMap<>();

    // 默认最小请求间隔（毫秒）
    private static final long DEFAULT_INTERVAL = 3000;

    public static boolean isRequestAllowed(String key) {
        return isRequestAllowed(key, DEFAULT_INTERVAL);
    }

    public static synchronized boolean isRequestAllowed(String key, long interval) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = REQUEST_TIME_MAP.get(key);

        if (lastTime == null || currentTime - lastTime >= interval) {
            REQUEST_TIME_MAP.put(key, currentTime);
            return true;
        }
        return false;
    }
}