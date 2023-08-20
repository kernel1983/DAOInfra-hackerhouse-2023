package com.bfit.recommand.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CustomOkHttpUtils {

    private static final OkHttpClient client;

    static {
        ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
//        builder.connectionPool(connectionPool);
        builder.addInterceptor(new LoggingInterceptor());
        client = builder
                .build();
    }

    public static String doGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    // 可以添加更多的请求方法，如POST、PUT、DELETE等
    public static String doPost(String url, String jsonBody) throws IOException {
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.nanoTime();
            log.info("Sending request: {}", request.url());

            Response response = chain.proceed(request);
            long endTime = System.nanoTime();
            log.info("Received response for: {} in {} ms",response.request().url(), (endTime - startTime) / 1e6);
            return response;
        }
    }

}

