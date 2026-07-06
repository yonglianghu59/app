package com.example.project_new_take_out.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.model.Shop;
import com.example.project_new_take_out.net.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 店铺数据仓库
 * 统一封装网络数据源和本地数据源，对上层屏蔽数据来源差异
 */
public class ShopRepository {

    private static volatile ShopRepository instance;

    private ShopRepository() {}

    public static ShopRepository getInstance() {
        if (instance == null) {
            synchronized (ShopRepository.class) {
                if (instance == null) {
                    instance = new ShopRepository();
                }
            }
        }
        return instance;
    }

    /**
     * 获取店铺列表
     */
    public LiveData<List<Shop>> getShopList() {
        MutableLiveData<List<Shop>> liveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApiService().getShopList()
                .enqueue(new Callback<List<Shop>>() {
                    @Override
                    public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(response.body());
                        } else {
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Shop>> call, Throwable t) {
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }

    /**
     * 获取店铺详情
     */
    public LiveData<Shop> getShopDetail(int shopId) {
        MutableLiveData<Shop> liveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApiService().getShopDetail(shopId)
                .enqueue(new Callback<Shop>() {
                    @Override
                    public void onResponse(Call<Shop> call, Response<Shop> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(response.body());
                        } else {
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<Shop> call, Throwable t) {
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }

    /**
     * 搜索店铺
     */
    public LiveData<List<Shop>> searchShops(String keyword) {
        MutableLiveData<List<Shop>> liveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApiService().searchShops(keyword)
                .enqueue(new Callback<List<Shop>>() {
                    @Override
                    public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(response.body());
                        } else {
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Shop>> call, Throwable t) {
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }
}
