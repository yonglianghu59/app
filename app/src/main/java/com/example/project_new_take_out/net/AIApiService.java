package com.example.project_new_take_out.net;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * DeepSeek AI API 接口
 */
public interface AIApiService {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    Call<Map<String, Object>> chatCompletion(
            @Header("Authorization") String auth,
            @Body Map<String, Object> body
    );
}
