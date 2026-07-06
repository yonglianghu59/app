package com.example.project_new_take_out.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * AI API 独立客户端（DeepSeek API，不使用 MockInterceptor）
 */
public class AIClient {

    private static final String BASE_URL = "https://api.deepseek.com/";
    private static final String API_KEY = "sk-d77189828ef34095afc0f92d53643210";

    private static volatile AIClient instance;
    private final AIApiService apiService;

    private AIClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AIApiService.class);
    }

    public static AIClient getInstance() {
        if (instance == null) {
            synchronized (AIClient.class) {
                if (instance == null) {
                    instance = new AIClient();
                }
            }
        }
        return instance;
    }

    public AIApiService getApiService() {
        return apiService;
    }

    public String getAuthHeader() {
        return "Bearer " + API_KEY;
    }
}
