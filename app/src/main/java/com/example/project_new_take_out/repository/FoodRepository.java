package com.example.project_new_take_out.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.model.Category;
import com.example.project_new_take_out.model.Food;
import com.example.project_new_take_out.net.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 菜品数据仓库
 */
public class FoodRepository {

    private static volatile FoodRepository instance;

    private FoodRepository() {}

    public static FoodRepository getInstance() {
        if (instance == null) {
            synchronized (FoodRepository.class) {
                if (instance == null) {
                    instance = new FoodRepository();
                }
            }
        }
        return instance;
    }

    /**
     * 获取店铺菜品分类及菜品
     */
    public LiveData<List<Category>> getShopCategories(int shopId) {
        MutableLiveData<List<Category>> liveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApiService().getShopCategories(shopId)
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(response.body());
                        } else {
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }

    /**
     * 获取菜品详情
     */
    public LiveData<Food> getFoodDetail(int foodId) {
        MutableLiveData<Food> liveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApiService().getFoodDetail(foodId)
                .enqueue(new Callback<Food>() {
                    @Override
                    public void onResponse(Call<Food> call, Response<Food> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(response.body());
                        } else {
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<Food> call, Throwable t) {
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }
}
