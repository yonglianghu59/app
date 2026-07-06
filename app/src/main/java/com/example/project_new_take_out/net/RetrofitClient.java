package com.example.project_new_take_out.net;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 网络请求单例
 * 统一配置超时、拦截器、日志、Mock
 */
public class RetrofitClient {

    // 预留后端接入地址（开发阶段使用 Mock，不对真实服务器发请求）
    private static final String BASE_URL = "https://api.example.com/";

    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 15;

    private static volatile RetrofitClient instance;
    private final Retrofit retrofit;
    private final ApiService apiService;
    private Context appContext;

    private RetrofitClient(Context context) {
        this.appContext = context.getApplicationContext();

        // 日志拦截器（开发阶段打印网络日志）
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Mock 拦截器 — 拦截所有请求返回本地 JSON 数据
        MockInterceptor mockInterceptor = new MockInterceptor(appContext);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(mockInterceptor) // Mock 拦截器最先匹配
                .retryOnConnectionFailure(true)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取单例实例（需先通过 Application 初始化）
     */
    public static RetrofitClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RetrofitClient 未初始化，请先在 Application 中调用 getInstance(Context)");
        }
        return instance;
    }

    /**
     * 初始化单例（Application.onCreate 中调用）
     */
    public static RetrofitClient getInstance(Context context) {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取 API 服务接口
     */
    public ApiService getApiService() {
        return apiService;
    }
}
