package com.example.project_new_take_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.model.Shop;
import com.example.project_new_take_out.repository.ShopRepository;

import java.util.List;

/**
 * 首页 ViewModel
 * 管理店铺列表数据、搜索状态
 */
public class MainViewModel extends AndroidViewModel {

    private final ShopRepository shopRepository;
    private final MutableLiveData<List<Shop>> shopListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmptyLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        shopRepository = ShopRepository.getInstance();
    }

    /**
     * 加载店铺列表
     */
    public void loadShopList() {
        isLoadingLiveData.setValue(true);
        LiveData<List<Shop>> result = shopRepository.getShopList();
        result.observeForever(shops -> {
            isLoadingLiveData.setValue(false);
            if (shops != null && !shops.isEmpty()) {
                shopListLiveData.setValue(shops);
                isEmptyLiveData.setValue(false);
            } else if (shops == null) {
                errorMessageLiveData.setValue("网络连接异常");
                isEmptyLiveData.setValue(false);
            } else {
                shopListLiveData.setValue(shops);
                isEmptyLiveData.setValue(true);
            }
        });
    }

    /**
     * 搜索店铺
     */
    public void searchShops(String keyword) {
        isLoadingLiveData.setValue(true);
        LiveData<List<Shop>> result = shopRepository.searchShops(keyword);
        result.observeForever(shops -> {
            isLoadingLiveData.setValue(false);
            if (shops != null) {
                shopListLiveData.setValue(shops);
                isEmptyLiveData.setValue(shops.isEmpty());
            }
        });
    }

    // ========== Getter ==========
    public LiveData<List<Shop>> getShopListLiveData() { return shopListLiveData; }
    public LiveData<Boolean> getIsLoadingLiveData() { return isLoadingLiveData; }
    public LiveData<Boolean> getIsEmptyLiveData() { return isEmptyLiveData; }
    public LiveData<String> getErrorMessageLiveData() { return errorMessageLiveData; }
}
