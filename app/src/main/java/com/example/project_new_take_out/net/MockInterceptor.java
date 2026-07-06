package com.example.project_new_take_out.net;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Mock 数据拦截器
 * 拦截所有 Retrofit 请求，返回 assets/mock 目录下的本地 JSON 数据
 * 开发阶段使用，对接真实后端时移除此拦截器
 */
public class MockInterceptor implements Interceptor {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final Context context;

    public MockInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();
        String method = request.method();

        // 根据请求路径匹配对应的 Mock JSON 文件
        String mockFileName = getMockFileName(path, method);
        String json = readMockJson(mockFileName);

        if (json != null) {
            return new Response.Builder()
                    .code(200)
                    .message("OK (Mock)")
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .body(ResponseBody.create(json, JSON_MEDIA_TYPE))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();
        }

        // 无匹配 Mock 数据时返回空数据
        String emptyJson = getEmptyResponse(path);
        return new Response.Builder()
                .code(200)
                .message("OK (Mock Empty)")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(emptyJson, JSON_MEDIA_TYPE))
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }

    /**
     * 根据 API 路径映射到 Mock 文件名（支持按 shop ID 区分）
     */
    private String getMockFileName(String path, String method) {
        // /api/shops/{id}/categories → mock/shop_categories_{id}.json
        java.util.regex.Matcher catMatcher = java.util.regex.Pattern.compile("/api/shops/(\\d+)/categories").matcher(path);
        if (catMatcher.matches()) {
            String shopId = catMatcher.group(1);
            return "mock/shop_categories_" + shopId + ".json";
        }
        // /api/shops/{id} → mock/shop_detail_{id}.json
        java.util.regex.Matcher detailMatcher = java.util.regex.Pattern.compile("/api/shops/(\\d+)").matcher(path);
        if (detailMatcher.matches()) {
            String shopId = detailMatcher.group(1);
            return "mock/shop_detail_" + shopId + ".json";
        }
        if (path.matches("/api/foods/\\d+")) {
            return "mock/food_detail.json";
        } else if (path.equals("/api/shops") || path.startsWith("/api/shops/search")) {
            return "mock/shops.json";
        } else if (path.matches("/api/categories/\\d+/foods")) {
            return "mock/foods_by_category.json";
        }
        return null;
    }

    /**
     * 读取 assets/mock/ 目录下的 JSON 文件
     * 使用 ByteArrayOutputStream 确保完整读取压缩过的资产文件
     */
    private String readMockJson(String fileName) {
        if (fileName == null) return null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = context.getAssets().open(fileName);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toString("UTF-8");
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (is != null) is.close();
                if (baos != null) baos.close();
            } catch (IOException ignored) {}
        }
    }

    /**
     * 返回空列表 JSON
     */
    private String getEmptyResponse(String path) {
        if (path.contains("categories") || path.contains("shops") || path.contains("foods")) {
            return "[]";
        }
        return "{}";
    }
}
