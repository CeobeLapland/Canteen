package com.example.canteen.net.manager;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RateLimitInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String key = request.url().encodedPath();

        if (!RequestRateLimiter.isRequestAllowed(key)) {
            throw new IOException("请求过于频繁，请稍后再试");
        }

        return chain.proceed(request);
    }
}