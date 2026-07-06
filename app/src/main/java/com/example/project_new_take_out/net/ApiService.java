package com.example.project_new_take_out.net;

import com.example.project_new_take_out.model.Category;
import com.example.project_new_take_out.model.Food;
import com.example.project_new_take_out.model.Shop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API 接口定义
 * RESTful API 端点映射
 */
public interface ApiService {

    // ========== 首页 ==========

    /**
     * 获取店铺列表
     */
    @GET("api/shops")
    Call<List<Shop>> getShopList();

    /**
     * 搜索店铺
     */
    @GET("api/shops/search")
    Call<List<Shop>> searchShops(@Query("keyword") String keyword);

    // ========== 店铺详情 ==========

    /**
     * 获取店铺详情
     */
    @GET("api/shops/{shopId}")
    Call<Shop> getShopDetail(@Path("shopId") int shopId);

    /**
     * 获取店铺菜品分类及菜品列表
     */
    @GET("api/shops/{shopId}/categories")
    Call<List<Category>> getShopCategories(@Path("shopId") int shopId);

    // ========== 菜品 ==========

    /**
     * 获取菜品详情
     */
    @GET("api/foods/{foodId}")
    Call<Food> getFoodDetail(@Path("foodId") int foodId);

    /**
     * 获取某分类下的菜品列表
     */
    @GET("api/categories/{categoryId}/foods")
    Call<List<Food>> getFoodsByCategory(@Path("categoryId") int categoryId);
}
